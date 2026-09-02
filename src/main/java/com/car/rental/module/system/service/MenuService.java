package com.car.rental.module.system.service;

import com.car.rental.entity.SysMenu;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<SysMenu> getTree();

    List<SysMenu> getFlatList();

    /**
     * 当前登录用户可见的扁平菜单列表（按用户权限过滤，供侧边栏渲染）。
     * 超级管理员（*）返回全部；无权限标识的菜单对所有登录用户可见；
     * 层级匹配：拥有 "settings" 权限即可见 "settings:users" 等子菜单。
     */
    List<SysMenu> getUserMenus();

    void addMenu(SysMenu menu);

    void updateMenu(SysMenu menu);

    void deleteMenu(Long id);

    void toggleStatus(Long id);

    List<Map<String, Object>> getPermissionTree();

    SysMenu getById(Long id);
}