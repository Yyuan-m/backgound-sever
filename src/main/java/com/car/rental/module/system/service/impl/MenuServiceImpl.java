package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.SysMenu;
import com.car.rental.mapper.SysMenuMapper;
import com.car.rental.module.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private com.car.rental.common.security.ButtonPermissionService buttonPermissionService;

    @Autowired
    private com.car.rental.module.auth.service.AuthService authService;

    @Autowired
    private com.car.rental.mapper.SysUserMapper sysUserMapper;

    /**
     * 菜单/按钮数据变更后，重建所有用户的 Redis 权限缓存。
     * （按钮被禁用/删除后，已登录用户的缓存权限需立即失效；禁止直接删 key，必须走 refreshUserCache）
     */
    private void refreshAllUserCaches() {
        try {
            for (com.car.rental.entity.SysUser user : sysUserMapper.selectList(null)) {
                authService.refreshUserCache(user.getId());
            }
        } catch (Exception e) {
            log.warn("刷新用户权限缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public List<SysMenu> getTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);

        List<SysMenu> rootMenus = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            }
        }

        for (SysMenu root : rootMenus) {
            buildChildren(root, allMenus);
        }

        return rootMenus;
    }

    private void buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() != null && menu.getParentId().equals(parent.getId())) {
                children.add(menu);
                buildChildren(menu, allMenus);
            }
        }
        parent.setChildren(children);
    }

    @Override
    public List<SysMenu> getFlatList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        return sysMenuMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> getUserMenus() {
        List<SysMenu> all = getFlatList();
        Collection<String> permissions = securityUtil.getCurrentUserPermissions();

        // 超级管理员返回全部
        if (permissions.contains("*")) {
            return all;
        }

        // 按权限过滤
        // - 菜单行：精确匹配 / 层级匹配（拥有 "settings" 即可见 "settings:users"）
        // - 按钮行（type='button'）：必须精确拥有且按钮处于启用状态（禁用按钮不下发）
        List<SysMenu> filtered = all.stream()
                .filter(menu -> {
                    String perm = menu.getPermission();
                    // 无权限标识的菜单对所有登录用户可见
                    if (perm == null || perm.isBlank()) {
                        return true;
                    }
                    // 按钮行：精确匹配 + 启用状态
                    if (com.car.rental.common.security.ButtonPermissionService.TYPE_BUTTON.equals(menu.getType())) {
                        return menu.getStatus() != null && menu.getStatus() == 1
                                && permissions.contains(perm);
                    }
                    return permissions.contains(perm)
                            || permissions.stream().anyMatch(a -> perm.startsWith(a + ":"));
                })
                .collect(java.util.stream.Collectors.toList());

        // 目录可见性推导：目录（无权限标识、有子菜单的节点）只有在任一子菜单可见时才下发，
        // 避免无权限用户看到空的目录分组（如车辆管理/系统设置）
        java.util.Set<Long> visibleIds = filtered.stream().map(SysMenu::getId).collect(java.util.stream.Collectors.toSet());
        return filtered.stream()
                .filter(menu -> {
                    boolean hasChildren = all.stream()
                            .anyMatch(x -> x.getParentId() != null && x.getParentId().equals(menu.getId()));
                    if (!hasChildren) {
                        return true; // 叶子节点按自身权限判断
                    }
                    // 有子节点：任一直接子菜单可见才保留
                    return all.stream()
                            .anyMatch(x -> x.getParentId() != null && x.getParentId().equals(menu.getId())
                                    && visibleIds.contains(x.getId()));
                })
                .toList();
    }

    @Override
    @Transactional
    public void addMenu(SysMenu menu) {
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.insert(menu);
        buttonPermissionService.evictCache();
        refreshAllUserCaches();
    }

    @Override
    @Transactional
    public void updateMenu(SysMenu menu) {
        SysMenu existingMenu = sysMenuMapper.selectById(menu.getId());
        if (existingMenu == null) {
            throw new BusinessException("菜单不存在");
        }

        existingMenu.setParentId(menu.getParentId());
        existingMenu.setName(menu.getName());
        existingMenu.setIcon(menu.getIcon());
        existingMenu.setPath(menu.getPath());
        existingMenu.setComponent(menu.getComponent());
        existingMenu.setPermission(menu.getPermission());
        existingMenu.setType(menu.getType());
        existingMenu.setSort(menu.getSort());
        existingMenu.setVisible(menu.getVisible());
        if (menu.getStatus() != null) {
            existingMenu.setStatus(menu.getStatus());
        }
        existingMenu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.updateById(existingMenu);
        buttonPermissionService.evictCache();
        refreshAllUserCaches();
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        if (menu.getIsCore() != null && menu.getIsCore() == 1) {
            throw new BusinessException("核心菜单不能删除");
        }

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (sysMenuMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该菜单下存在子菜单，无法删除");
        }

        sysMenuMapper.deleteById(id);
        buttonPermissionService.evictCache();
        refreshAllUserCaches();
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        if (menu.getIsCore() != null && menu.getIsCore() == 1) {
            throw new BusinessException("核心菜单不能禁用");
        }
        menu.setStatus(menu.getStatus() == 1 ? 0 : 1);
        menu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.updateById(menu);
        buttonPermissionService.evictCache();
        refreshAllUserCaches();
    }

    @Override
    public List<Map<String, Object>> getPermissionTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);

        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == null) {
                Map<String, Object> node = buildPermissionNode(menu, allMenus);
                tree.add(node);
            }
        }
        return tree;
    }

    private Map<String, Object> buildPermissionNode(SysMenu menu, List<SysMenu> allMenus) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", menu.getPermission() != null ? menu.getPermission() : "menu_" + menu.getId());
        node.put("title", menu.getName());
        // 节点类型（menu=菜单 / button=按钮），供前端权限树渲染区分标识
        node.put("type", menu.getType());

        List<Map<String, Object>> children = new ArrayList<>();
        for (SysMenu child : allMenus) {
            if (child.getParentId() != null && child.getParentId().equals(menu.getId())) {
                children.add(buildPermissionNode(child, allMenus));
            }
        }
        if (!children.isEmpty()) {
            node.put("children", children);
        }

        return node;
    }

    @Override
    public SysMenu getById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return menu;
    }
}