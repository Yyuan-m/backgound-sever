package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.SysRole;
import com.car.rental.entity.SysUser;
import com.car.rental.entity.SysUserRole;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.mapper.SysUserRoleMapper;
import com.car.rental.module.auth.service.AuthService;
import com.car.rental.module.system.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private static final String SUPER_ADMIN_ROLE_KEY = "super_admin";

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private AuthService authService;

    @Override
    public IPage<SysRole> getPageList(long pageNum, long pageSize, String keyword, Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysRole::getName, keyword)
                    .or().like(SysRole::getRoleKey, keyword));
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }
        wrapper.orderByDesc(SysRole::getCreatedAt);
        return sysRoleMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void addRole(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, role.getRoleKey());
        if (sysRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        LambdaQueryWrapper<SysRole> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SysRole::getName, role.getName());
        if (sysRoleMapper.selectCount(nameWrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        role.setStatus(1);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        sysRoleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(SysRole role) {
        SysRole existingRole = sysRoleMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new BusinessException("角色不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(existingRole.getRoleKey())) {
            throw new BusinessException("不能修改超级管理员角色");
        }

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        if (role.getStatus() != null) {
            existingRole.setStatus(role.getStatus());
        }
        existingRole.setUpdatedAt(LocalDateTime.now());
        sysRoleMapper.updateById(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            throw new BusinessException("不能删除超级管理员角色");
        }

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, id);
        List<SysUserRole> links = sysUserRoleMapper.selectList(wrapper);
        if (!links.isEmpty()) {
            // 仅当存在「现存用户」的关联时才拦截；历史遗留的孤立关联（用户已删除但关联未清理）
            // 不阻止删除，并在删除角色时顺带清理干净
            List<Long> userIds = links.stream().map(SysUserRole::getUserId).toList();
            Long activeUsers = sysUserMapper.selectCount(
                    // @TableLogic 自动排除已软删用户
                    new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds));
            if (activeUsers != null && activeUsers > 0) {
                throw new BusinessException("该角色下存在关联用户，无法删除");
            }
            // 纯孤立关联：删除角色前清理，避免残留
            sysUserRoleMapper.delete(wrapper);
        }

        sysRoleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            throw new BusinessException("不能禁用超级管理员角色");
        }
        role.setStatus(role.getStatus() == 1 ? 0 : 1);
        role.setUpdatedAt(LocalDateTime.now());
        sysRoleMapper.updateById(role);
    }

    @Override
    public List<String> getPermissions(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role.getMenuPermissions() != null ? role.getMenuPermissions() : new ArrayList<>();
    }

    @Override
    @Transactional
    public void savePermissions(Long id, List<String> permissions) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            throw new BusinessException("不能修改超级管理员角色的权限");
        }
        role.setMenuPermissions(permissions);
        role.setUpdatedAt(LocalDateTime.now());
        sysRoleMapper.updateById(role);

        // 重建拥有该角色的所有用户的 Redis 权限缓存，权限变更立即生效
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        for (SysUserRole ur : userRoles) {
            authService.refreshUserCache(ur.getUserId());
        }
        log.info("角色 {} 权限已更新，已刷新 {} 个用户的权限缓存", role.getName(), userRoles.size());
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }
}