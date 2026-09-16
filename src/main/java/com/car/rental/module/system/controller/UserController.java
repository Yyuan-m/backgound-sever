package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "用户管理", description = "后台用户的增删改查、启停用、批量操作与密码重置/修改")
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequirePermission("settings:user:list")
    @Operation(summary = "用户列表（分页）", description = "说明：分页查询用户，按创建时间倒序；返回结果中密码字段统一置空。需要 settings:user:list 权限")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：用户名/昵称/手机号模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选（1 启用 / 0 禁用）") @RequestParam(required = false) Integer status,
            @Parameter(description = "角色标识精确筛选") @RequestParam(required = false) String role) {
        IPage<SysUser> page = userService.getPageList(pageNum, pageSize, keyword, status, role);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(PageResult.of(page));
    }

    @RequirePermission("settings:user:list")
    @Operation(summary = "用户详情", description = "说明：按 ID 查询用户完整信息。需要 settings:user:list 权限")
    @GetMapping("/detail/{id}")
    public Result<SysUser> detail(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @RequirePermission("settings:user:add")
    @Operation(summary = "新增用户", description = "说明：用户名唯一校验，重复报错；新增后默认启用（status=1），并同步维护主角色与角色列表。需要 settings:user:add 权限")
    @PostMapping("/add")
    @LogChanges(
        entityClass = SysUser.class,
        mapperClass = SysUserMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "password"},
        fieldLabels = {
            "username:用户名", "nickname:昵称", "email:邮箱", "phone:手机号",
            "avatar:头像", "role:角色", "roleName:角色名称", "roles:角色列表",
            "status:状态", "lastLoginIp:最后登录IP", "lastLoginTime:最后登录时间"
        }
    )
    public Result<Void> add(@RequestBody SysUser user) {
        userService.addUser(user);
        return Result.ok();
    }

    @RequirePermission("settings:user:update")
    @Operation(summary = "编辑用户", description = "说明：按字段非空更新基本信息；超级管理员不允许修改。需要 settings:user:update 权限")
    @PutMapping("/update")
    @LogChanges(
        entityClass = SysUser.class,
        mapperClass = SysUserMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "password"},
        fieldLabels = {
            "username:用户名", "nickname:昵称", "email:邮箱", "phone:手机号",
            "avatar:头像", "role:角色", "roleName:角色名称", "roles:角色列表",
            "status:状态", "lastLoginIp:最后登录IP", "lastLoginTime:最后登录时间"
        }
    )
    public Result<Void> update(@RequestBody SysUser user) {
        userService.updateUser(user);
        return Result.ok();
    }

    @RequirePermission("settings:user:delete")
    @Operation(summary = "删除用户", description = "说明：超级管理员不允许删除；删除时同步清除角色关联与该用户的权限缓存。需要 settings:user:delete 权限")
    @DeleteMapping("/delete/{id}")
    @LogChanges(
        entityClass = SysUser.class,
        mapperClass = SysUserMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete", "password"},
        fieldLabels = {
            "username:用户名", "nickname:昵称", "email:邮箱", "phone:手机号",
            "avatar:头像", "role:角色", "roleName:角色名称", "roles:角色列表",
            "status:状态", "lastLoginIp:最后登录IP", "lastLoginTime:最后登录时间"
        }
    )
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @RequirePermission("settings:user:status")
    @Operation(summary = "启用/禁用用户", description = "说明：状态 1/0 切换。需要 settings:user:status 权限")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:user:reset-password")
    @Operation(summary = "重置用户密码", description = "说明：body 中 password 可选，为空时重置为系统默认密码。需要 settings:user:reset-password 权限")
    @PutMapping("/reset-password/{id}")
    public Result<Void> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String newPassword = body != null ? body.get("password") : null;
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @RequirePermission("settings:user:delete")
    @Operation(summary = "批量删除用户", description = "说明：body 传 ids 用户ID数组；超级管理员自动跳过不删除。需要 settings:user:delete 权限")
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        userService.batchDelete(ids);
        return Result.ok();
    }

    @RequirePermission("settings:user:status")
    @Operation(summary = "批量启用/禁用用户", description = "说明：body 传 ids 用户ID数组与 status（1 启用 / 0 禁用）；超级管理员自动跳过。需要 settings:user:status 权限")
    @PostMapping("/batch-status")
    public Result<Void> batchToggleStatus(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) body.get("ids");
        Integer status = (Integer) body.get("status");
        userService.batchToggleStatus(ids, status);
        return Result.ok();
    }

    @RequirePermission("settings:user:reset-password")
    @Operation(summary = "修改密码（校验原密码）", description = "说明：body 传 userId/oldPassword/newPassword；原密码校验失败时报错。需要 settings:user:reset-password 权限")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.ok();
    }
}