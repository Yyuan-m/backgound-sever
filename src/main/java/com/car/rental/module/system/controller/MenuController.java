package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysMenu;
import com.car.rental.mapper.SysMenuMapper;
import com.car.rental.module.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @RequirePermission("settings:menu:list")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> tree = menuService.getTree();
        return Result.ok(tree);
    }

    @RequirePermission("settings:menu:list")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        List<SysMenu> list = menuService.getFlatList();
        return Result.ok(list);
    }

    @RequirePermission("settings:menu:list")
    @GetMapping("/detail/{id}")
    public Result<SysMenu> detail(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @RequirePermission("settings:menu:add")
    @PostMapping("/add")
    @LogChanges(
        entityClass = SysMenu.class,
        mapperClass = SysMenuMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "children"},
        fieldLabels = {
            "parentId:父级ID", "name:名称", "icon:图标", "path:路径",
            "component:组件", "permission:权限标识", "type:类型",
            "sort:排序", "status:状态", "visible:是否可见", "isCore:是否核心"
        }
    )
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.addMenu(menu);
        return Result.ok();
    }

    @RequirePermission("settings:menu:update")
    @PutMapping("/update")
    @LogChanges(
        entityClass = SysMenu.class,
        mapperClass = SysMenuMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "children"},
        fieldLabels = {
            "parentId:父级ID", "name:名称", "icon:图标", "path:路径",
            "component:组件", "permission:权限标识", "type:类型",
            "sort:排序", "status:状态", "visible:是否可见", "isCore:是否核心"
        }
    )
    public Result<Void> update(@RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return Result.ok();
    }

    @RequirePermission("settings:menu:delete")
    @DeleteMapping("/delete/{id}")
    @LogChanges(
        entityClass = SysMenu.class,
        mapperClass = SysMenuMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "children"},
        fieldLabels = {
            "parentId:父级ID", "name:名称", "icon:图标", "path:路径",
            "component:组件", "permission:权限标识", "type:类型",
            "sort:排序", "status:状态", "visible:是否可见", "isCore:是否核心"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }

    @RequirePermission("settings:menu:status")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        menuService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:menu:list")
    @GetMapping("/permission-tree")
    public Result<List<Map<String, Object>>> permissionTree() {
        List<Map<String, Object>> tree = menuService.getPermissionTree();
        return Result.ok(tree);
    }
}