package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysMenu;
import com.car.rental.mapper.SysMenuMapper;
import com.car.rental.module.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "菜单管理", description = "系统菜单与权限标识的维护，提供菜单树/平铺列表、当前用户可见菜单及角色授权用权限树")
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 当前登录用户可见的菜单列表（侧边栏渲染数据源）。
     * 仅需登录即可访问（无 @RequirePermission），由服务层按用户权限过滤，
     * 避免无 settings 权限的角色（如客服/财务）加载菜单被 403 拦截导致侧边栏空白。
     */
    @Operation(summary = "当前用户可见菜单", description = "说明：返回按当前登录用户权限过滤后的菜单列表（侧边栏渲染数据源）；无权限标识的菜单对所有登录用户可见，按钮行需精确拥有权限且按钮启用，超级管理员返回全部。仅需登录即可访问（无权限要求），避免无 settings 权限的角色加载菜单被 403 拦截导致侧边栏空白")
    @GetMapping("/user-menus")
    public Result<List<SysMenu>> userMenus() {
        return Result.ok(menuService.getUserMenus());
    }

    @RequirePermission("settings:menu:list")
    @Operation(summary = "菜单树（全量）", description = "说明：返回全部菜单按 parent/children 组装的树形结构（节点含 children 子菜单数组），用于菜单管理页展示。需要 settings:menu:list 权限")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> tree = menuService.getTree();
        return Result.ok(tree);
    }

    @RequirePermission("settings:menu:list")
    @Operation(summary = "菜单平铺列表", description = "说明：返回全部菜单的扁平列表（不组装 children 层级），用于父级菜单选择等场景。需要 settings:menu:list 权限")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        List<SysMenu> list = menuService.getFlatList();
        return Result.ok(list);
    }

    @RequirePermission("settings:menu:list")
    @Operation(summary = "菜单详情", description = "说明：按 ID 查询单个菜单的完整信息。需要 settings:menu:list 权限")
    @GetMapping("/detail/{id}")
    public Result<SysMenu> detail(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @RequirePermission("settings:menu:add")
    @Operation(summary = "新增菜单", description = "说明：新增菜单/目录/按钮节点，parentId 指定父级；保存后自动清理按钮权限缓存并刷新全部用户菜单权限缓存，立即生效。需要 settings:menu:add 权限")
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
    @Operation(summary = "编辑菜单", description = "说明：修改菜单基本信息，菜单不存在时报错；保存后自动清理按钮权限缓存并刷新全部用户缓存。需要 settings:menu:update 权限")
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
    @Operation(summary = "删除菜单", description = "说明：核心菜单（isCore=1）不允许删除；该菜单下存在子菜单时也无法删除。需要 settings:menu:delete 权限")
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
    public Result<Void> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }

    @RequirePermission("settings:menu:status")
    @Operation(summary = "启用/禁用菜单", description = "说明：状态 1/0 切换；核心菜单（isCore=1）不允许禁用；切换后清理按钮权限缓存并刷新全部用户缓存。需要 settings:menu:status 权限")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:menu:list")
    @Operation(summary = "权限树（角色授权用）", description = "说明：返回全部菜单组成的权限树，节点结构 {key: 权限标识（无标识时 menu_ID）, title: 菜单名, type: menu/button, children: 子节点数组}，按 sort 升序，供角色权限勾选树渲染。需要 settings:menu:list 权限")
    @GetMapping("/permission-tree")
    public Result<List<Map<String, Object>>> permissionTree() {
        List<Map<String, Object>> tree = menuService.getPermissionTree();
        return Result.ok(tree);
    }
}