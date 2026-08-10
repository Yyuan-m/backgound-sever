package com.car.rental.module.auth.service.impl;

import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.auth.model.UserVO;
import com.car.rental.module.auth.service.AuthService;
import com.car.rental.module.auth.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final SysUserMapper sysUserMapper;
    private final AuthService authService;

    @Override
    public UserVO getInfo(Long userId) {
        // 复用 AuthService 的 getUserInfo，确保权限与缓存同步
        return authService.getUserInfo(userId);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname, String email, String phone) {
        SysUser existing = sysUserMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (StringUtils.hasText(nickname)) {
            existing.setNickname(nickname);
        }
        if (StringUtils.hasText(email)) {
            existing.setEmail(email);
        }
        if (StringUtils.hasText(phone)) {
            existing.setPhone(phone);
        }
        existing.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(existing);
        // 同步刷新 Redis 缓存
        authService.getUserInfo(userId);
    }

    @Override
    @Transactional
    public void updateAvatar(Long userId, String avatar) {
        SysUser existing = sysUserMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        existing.setAvatar(avatar);
        existing.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(existing);
        // 同步刷新 Redis 缓存
        authService.getUserInfo(userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser existing = sysUserMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (!StringUtils.hasText(oldPassword) || !oldPassword.equals(existing.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BusinessException("新密码至少6个字符");
        }
        existing.setPassword(newPassword);
        existing.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(existing);
    }
}
