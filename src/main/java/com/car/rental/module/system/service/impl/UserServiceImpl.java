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
import com.car.rental.module.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String SUPER_ADMIN_ROLE_KEY = "super_admin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_ROLE = "operator";

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<SysUser> getPageList(long pageNum, long pageSize, String keyword, Integer status, String role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void addUser(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        user.setPassword(user.getPassword());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 同步主角色 role / roleName / roles
        syncRoleFields(user);

        sysUserMapper.insert(user);
    }

    /**
     * 根据传入的 roles 或 role 同步主角色字段：
     * - 若 roles 非空，取第一个作为主 role，并查询角色名称
     * - 若仅传了 role，则将 roles 设为 [role]
     */
    private void syncRoleFields(SysUser user) {
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String primaryRole = user.getRoles().get(0);
            user.setRole(primaryRole);
            if (!StringUtils.hasText(user.getRoleName())) {
                SysRole roleEntity = sysRoleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, primaryRole));
                user.setRoleName(roleEntity != null ? roleEntity.getName() : primaryRole);
            }
        } else if (StringUtils.hasText(user.getRole())) {
            user.setRoles(List.of(user.getRole()));
            if (!StringUtils.hasText(user.getRoleName())) {
                SysRole roleEntity = sysRoleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, user.getRole()));
                user.setRoleName(roleEntity != null ? roleEntity.getName() : user.getRole());
            }
        } else {
            user.setRole(DEFAULT_ROLE);
            user.setRoles(List.of(DEFAULT_ROLE));
            user.setRoleName("操作员");
        }
    }

    @Override
    @Transactional
    public void updateUser(SysUser user) {
        SysUser existingUser = sysUserMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(existingUser.getRole())) {
            throw new BusinessException("不能修改超级管理员");
        }

        if (StringUtils.hasText(user.getNickname())) {
            existingUser.setNickname(user.getNickname());
        }
        if (StringUtils.hasText(user.getPhone())) {
            existingUser.setPhone(user.getPhone());
        }
        if (StringUtils.hasText(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }
        // 同步角色字段
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            existingUser.setRoles(user.getRoles());
            existingUser.setRole(user.getRoles().get(0));
            SysRole roleEntity = sysRoleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, user.getRoles().get(0)));
            existingUser.setRoleName(roleEntity != null ? roleEntity.getName() : user.getRoles().get(0));
        } else if (StringUtils.hasText(user.getRole())) {
            existingUser.setRole(user.getRole());
            existingUser.setRoles(List.of(user.getRole()));
            if (StringUtils.hasText(user.getRoleName())) {
                existingUser.setRoleName(user.getRoleName());
            } else {
                SysRole roleEntity = sysRoleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, user.getRole()));
                existingUser.setRoleName(roleEntity != null ? roleEntity.getName() : user.getRole());
            }
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        existingUser.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(user.getRole())) {
            throw new BusinessException("不能删除超级管理员");
        }
        sysUserMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (SUPER_ADMIN_ROLE_KEY.equals(user.getRole())) {
            throw new BusinessException("不能禁用超级管理员");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(newPassword != null && !newPassword.isEmpty() ? newPassword : DEFAULT_PASSWORD);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(ids);
        List<Long> deletableIds = users.stream()
                .filter(u -> !SUPER_ADMIN_ROLE_KEY.equals(u.getRole()))
                .map(SysUser::getId)
                .toList();
        if (!deletableIds.isEmpty()) {
            sysUserMapper.deleteBatchIds(deletableIds);
        }
    }

    @Override
    @Transactional
    public void batchToggleStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(ids);
        List<Long> updatableIds = users.stream()
                .filter(u -> !SUPER_ADMIN_ROLE_KEY.equals(u.getRole()))
                .map(SysUser::getId)
                .toList();
        for (Long uid : updatableIds) {
            SysUser user = new SysUser();
            user.setId(uid);
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            sysUserMapper.updateById(user);
        }
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }
}