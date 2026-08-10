package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.SysRole;
import com.car.rental.entity.SysUserRole;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.mapper.SysUserRoleMapper;
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
        if (sysUserRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该角色下存在关联用户，无法删除");
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