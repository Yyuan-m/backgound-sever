package com.car.rental.module.auth.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.module.auth.model.UserVO;
import com.car.rental.module.auth.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 个人中心控制器
 * 所有接口均通过 SecurityUtil.getCurrentUserId() 取当前登录用户，
 * 杜绝前端传 userId 越权改他人资料。
 */
@Tag(name = "个人中心", description = "当前登录用户的资料查看与修改、头像更新、修改密码")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "当前用户信息", description = "需登录。返回当前登录用户的详细信息（昵称、邮箱、手机号、头像等）")
    @GetMapping("/info")
    public Result<UserVO> getInfo() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return Result.ok(profileService.getInfo(userId));
    }

    @Operation(summary = "修改个人资料", description = "需登录。仅能修改自己的资料，支持 nickname 昵称、email 邮箱、phone 手机号，传 null 的字段不修改")
    @PutMapping("/update")
    public Result<Void> updateProfile(@RequestBody Map<String, Object> body) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        String nickname = body.get("nickname") != null ? String.valueOf(body.get("nickname")) : null;
        String email = body.get("email") != null ? String.valueOf(body.get("email")) : null;
        String phone = body.get("phone") != null ? String.valueOf(body.get("phone")) : null;
        profileService.updateProfile(userId, nickname, email, phone);
        return Result.ok();
    }

    @Operation(summary = "更新头像", description = "需登录。更新当前登录用户的头像地址")
    @PostMapping("/avatar")
    public Result<Void> updateAvatar(@RequestBody Map<String, Object> body) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        String avatar = body.get("avatar") != null ? String.valueOf(body.get("avatar")) : null;
        profileService.updateAvatar(userId, avatar);
        return Result.ok();
    }

    @Operation(summary = "修改密码", description = "需登录。校验旧密码 oldPassword 后设置新密码 newPassword，仅能修改自己的密码")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        profileService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }
}
