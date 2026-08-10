package com.car.rental.module.auth.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.module.auth.model.UserVO;
import com.car.rental.module.auth.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 个人中心控制器
 * 所有接口均通过 SecurityUtil.getCurrentUserId() 取当前登录用户，
 * 杜绝前端传 userId 越权改他人资料。
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final SecurityUtil securityUtil;

    @GetMapping("/info")
    public Result<UserVO> getInfo() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return Result.ok(profileService.getInfo(userId));
    }

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
