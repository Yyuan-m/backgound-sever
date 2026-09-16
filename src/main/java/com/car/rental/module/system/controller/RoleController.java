package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysRole;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.module.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "系统角色的增删改查、启停用与菜单权限分配，权限变更后实时刷新用户缓存")
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequirePermission("settings:role:list")
    @Operation(summary = "角色列表（分页）", description = "说明：分页查询角色，按创建时间倒序；支持关键字与状态筛选。需要 settings:role:list 权限")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：角色名称/角色标识模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选（1 启用 / 0 禁用）") @RequestParam(required = false) Integer status) {
        IPage<SysRole> page = roleService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @RequirePermission("settings:role:list")
    @Operation(summary = "角色详情", description = "说明：按 ID 查询单个角色信息。需要 settings:role:list 权限")
    @GetMapping("/detail/{id}")
    public Result<SysRole> detail(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @RequirePermission("settings:role:add")
    @Operation(summary = "新增角色", description = "说明：角色标识 roleKey 与角色名称均唯一，重复时报错；新增后默认启用（status=1）。需要 settings:role:add 权限")
    @PostMapping("/add")
    @LogChanges(
        entityClass = SysRole.class,
        mapperClass = SysRoleMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "roleKey:角色标识", "status:状态",
            "description:描述", "menuPermissions:菜单权限"
        }
    )
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.addRole(role);
        return Result.ok();
    }

    @RequirePermission("settings:role:update")
    @Operation(summary = "编辑角色", description = "说明：修改角色名称/描述/状态；超级管理员角色不允许修改。需要 settings:role:update 权限")
    @PutMapping("/update")
    @LogChanges(
        entityClass = SysRole.class,
        mapperClass = SysRoleMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "roleKey:角色标识", "status:状态",
            "description:描述", "menuPermissions:菜单权限"
        }
    )
    public Result<Void> update(@RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.ok();
    }

    @RequirePermission("settings:role:delete")
    @Operation(summary = "删除角色", description = "说明：超级管理员角色不允许删除；仍有现存用户使用该角色时拦截删除，用户已删除的孤立关联会顺带清理。需要 settings:role:delete 权限")
    @DeleteMapping("/delete/{id}")
    @LogChanges(
        entityClass = SysRole.class,
        mapperClass = SysRoleMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "roleKey:角色标识", "status:状态",
            "description:描述", "menuPermissions:菜单权限"
        }
    )
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @RequirePermission("settings:role:status")
    @Operation(summary = "启用/禁用角色", description = "说明：状态 1/0 切换；超级管理员角色不允许禁用。需要 settings:role:status 权限")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:role:permission")
    @Operation(summary = "查询角色权限", description = "说明：返回该角色已勾选的权限标识字符串列表。需要 settings:role:permission 权限")
    @GetMapping("/permissions/{id}")
    public Result<List<String>> getPermissions(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<String> permissions = roleService.getPermissions(id);
        return Result.ok(permissions);
    }

    @RequirePermission("settings:role:permission")
    @Operation(summary = "保存角色权限", description = "说明：传入权限标识列表整体覆盖该角色的权限；超级管理员角色不允许修改；保存后重建拥有该角色的所有用户的 Redis 权限缓存，权限变更立即生效。需要 settings:role:permission 权限")
    @PutMapping("/permissions/{id}")
    public Result<Void> savePermissions(@Parameter(description = "角色ID") @PathVariable Long id, @RequestBody List<String> permissions) {
        roleService.savePermissions(id, permissions);
        return Result.ok();
    }
}