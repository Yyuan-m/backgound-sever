package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysRole;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.module.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequirePermission("settings:role:list")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<SysRole> page = roleService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @RequirePermission("settings:role:list")
    @GetMapping("/detail/{id}")
    public Result<SysRole> detail(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @RequirePermission("settings:role:add")
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
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @RequirePermission("settings:role:status")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        roleService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:role:permission")
    @GetMapping("/permissions/{id}")
    public Result<List<String>> getPermissions(@PathVariable Long id) {
        List<String> permissions = roleService.getPermissions(id);
        return Result.ok(permissions);
    }

    @RequirePermission("settings:role:permission")
    @PutMapping("/permissions/{id}")
    public Result<Void> savePermissions(@PathVariable Long id, @RequestBody List<String> permissions) {
        roleService.savePermissions(id, permissions);
        return Result.ok();
    }
}