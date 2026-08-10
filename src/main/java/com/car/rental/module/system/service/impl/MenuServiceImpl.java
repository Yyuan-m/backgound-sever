package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.SysMenu;
import com.car.rental.mapper.SysMenuMapper;
import com.car.rental.module.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

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
    @Transactional
    public void addMenu(SysMenu menu) {
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.insert(menu);
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