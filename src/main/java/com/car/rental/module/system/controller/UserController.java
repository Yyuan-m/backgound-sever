package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequirePermission("settings:user:list")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String role) {
        IPage<SysUser> page = userService.getPageList(pageNum, pageSize, keyword, status, role);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(PageResult.of(page));
    }

    @RequirePermission("settings:user:list")
    @GetMapping("/detail/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @RequirePermission("settings:user:add")
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
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @RequirePermission("settings:user:status")
    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        userService.toggleStatus(id);
        return Result.ok();
    }

    @RequirePermission("settings:user:reset-password")
    @PutMapping("/reset-password/{id}")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String newPassword = body != null ? body.get("password") : null;
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @RequirePermission("settings:user:delete")
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        userService.batchDelete(ids);
        return Result.ok();
    }

    @RequirePermission("settings:user:status")
    @PostMapping("/batch-status")
    public Result<Void> batchToggleStatus(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) body.get("ids");
        Integer status = (Integer) body.get("status");
        userService.batchToggleStatus(ids, status);
        return Result.ok();
    }

    @RequirePermission("settings:user:reset-password")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.ok();
    }
}