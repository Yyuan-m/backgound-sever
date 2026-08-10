package com.car.rental.module.system.service;

import com.car.rental.entity.SysMenu;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<SysMenu> getTree();

    List<SysMenu> getFlatList();

    void addMenu(SysMenu menu);

    void updateMenu(SysMenu menu);

    void deleteMenu(Long id);

    void toggleStatus(Long id);

    List<Map<String, Object>> getPermissionTree();

    SysMenu getById(Long id);
}