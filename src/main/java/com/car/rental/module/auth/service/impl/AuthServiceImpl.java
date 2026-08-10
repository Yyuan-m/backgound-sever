package com.car.rental.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.JwtUtil;
import com.car.rental.entity.SysRole;
import com.car.rental.entity.SysUser;
import com.car.rental.entity.SysUserRole;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.mapper.SysUserRoleMapper;
import com.car.rental.module.auth.controller.LoginDTO;
import com.car.rental.module.auth.controller.RegisterDTO;
import com.car.rental.module.auth.model.UserVO;
import com.car.rental.module.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REDIS_USER_INFO_PREFIX = "user:info:";
    private static final String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REDIS_PWD_RESET_PREFIX = "pwd:reset:";
    private static final long PWD_RESET_EXPIRE_MINUTES = 5L;

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        List<String> permissions = loadUserPermissions(user.getId());
        cacheUserInfo(user, permissions);

        UserVO userVO = buildUserVO(user, permissions);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("user", userVO);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();

        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(registerDTO.getPassword());
        user.setNickname(StringUtils.hasText(registerDTO.getName()) ? registerDTO.getName() : username);
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole("operator");
        user.setRoleName("运营管理员");
        user.setRoles(List.of("operator"));
        user.setStatus(1);

        sysUserMapper.insert(user);

        SysRole operatorRole = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, "operator")
        );
        if (operatorRole != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(operatorRole.getId());
            sysUserRoleMapper.insert(userRole);
        }

        log.info("用户注册成功: username={}, userId={}", username, user.getId());
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        // 拉黑 access token（剩余有效期）
        if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
            long remaining = jwtUtil.getExpiration();
            stringRedisTemplate.opsForValue().set(
                    REDIS_TOKEN_BLACKLIST_PREFIX + accessToken,
                    "1",
                    remaining,
                    TimeUnit.MILLISECONDS
            );
        }
        // 拉黑 refresh token（剩余有效期），刷新令牌随之失效
        if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
            long remaining = jwtUtil.getRefreshExpiration();
            stringRedisTemplate.opsForValue().set(
                    REDIS_TOKEN_BLACKLIST_PREFIX + refreshToken,
                    "1",
                    remaining,
                    TimeUnit.MILLISECONDS
            );
        }
        log.info("用户登出，access/refresh token 已加入黑名单");
    }

    @Override
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("刷新令牌不能为空");
        }
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("登录信息已过期，请重新登录");
        }
        // 必须是 refresh 类型，防止 access token 被误用为刷新凭证
        if (!JwtUtil.TOKEN_TYPE_REFRESH.equals(jwtUtil.getTokenType(refreshToken))) {
            throw new BusinessException("令牌类型错误");
        }
        if (isTokenBlacklisted(refreshToken)) {
            throw new BusinessException("登录信息已失效，请重新登录");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("用户不存在或已被禁用");
        }

        // 刷新权限缓存，保证刷新后权限为最新
        List<String> permissions = loadUserPermissions(userId);
        cacheUserInfo(user, permissions);

        String newAccessToken = jwtUtil.generateToken(userId, username);
        // 滚动刷新 refresh token（旧 refresh 立即失效），降低泄漏后的重放风险
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);
        long remaining = jwtUtil.getRefreshExpiration();
        stringRedisTemplate.opsForValue().set(
                REDIS_TOKEN_BLACKLIST_PREFIX + refreshToken,
                "1",
                remaining,
                TimeUnit.MILLISECONDS
        );

        Map<String, Object> result = new HashMap<>();
        result.put("token", newAccessToken);
        result.put("refreshToken", newRefreshToken);
        return result;
    }

    private boolean isTokenBlacklisted(String token) {
        Boolean hasKey = stringRedisTemplate.hasKey(REDIS_TOKEN_BLACKLIST_PREFIX + token);
        return Boolean.TRUE.equals(hasKey);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> permissions = loadUserPermissions(userId);

        cacheUserInfo(user, permissions);

        return buildUserVO(user, permissions);
    }

    private List<String> loadUserPermissions(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, 1)
        );

        List<String> allPermissions = new ArrayList<>();
        for (SysRole role : roles) {
            if (role.getMenuPermissions() != null) {
                for (String perm : role.getMenuPermissions()) {
                    if (!allPermissions.contains(perm)) {
                        allPermissions.add(perm);
                    }
                }
            }
        }

        return allPermissions;
    }

    private void cacheUserInfo(SysUser user, List<String> permissions) {
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put("id", user.getId());
        cacheMap.put("username", user.getUsername());
        cacheMap.put("nickname", user.getNickname());
        cacheMap.put("email", user.getEmail());
        cacheMap.put("phone", user.getPhone());
        cacheMap.put("avatar", user.getAvatar());
        cacheMap.put("role", user.getRole());
        cacheMap.put("roleName", user.getRoleName());
        cacheMap.put("status", user.getStatus());
        if (permissions != null) {
            cacheMap.put("permissions", permissions);
        }

        long expiration = jwtUtil.getExpiration();
        redisTemplate.opsForValue().set(
                REDIS_USER_INFO_PREFIX + user.getId(),
                cacheMap,
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    private UserVO buildUserVO(SysUser user, List<String> permissions) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .roleName(user.getRoleName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginTime(user.getLastLoginTime())
                .permissions(permissions)
                .build();
    }

    @Override
    public Map<String, Object> verifyForgotPassword(String username, String email) {
        SysUser user = sysUserMapper.findByUsernameAndEmail(username, email);
        if (user == null) {
            throw new BusinessException("用户名或邮箱不正确");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        // 生成一次性重置令牌，5 分钟有效
        String resetToken = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(
                REDIS_PWD_RESET_PREFIX + resetToken,
                user.getUsername(),
                PWD_RESET_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );
        Map<String, Object> result = new HashMap<>();
        result.put("resetToken", resetToken);
        result.put("expireMinutes", PWD_RESET_EXPIRE_MINUTES);
        return result;
    }

    @Override
    public void resetPassword(String username, String resetToken, String newPassword) {
        String cacheKey = REDIS_PWD_RESET_PREFIX + resetToken;
        String cachedUsername = stringRedisTemplate.opsForValue().get(cacheKey);
        if (!StringUtils.hasText(cachedUsername) || !cachedUsername.equals(username)) {
            throw new BusinessException("重置令牌无效或已过期，请重新验证");
        }
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setPassword(newPassword);
        sysUserMapper.updateById(update);
        // 重置后立即失效令牌
        stringRedisTemplate.delete(cacheKey);
        log.info("用户 {} 通过找回密码功能重置密码成功", username);
    }
}