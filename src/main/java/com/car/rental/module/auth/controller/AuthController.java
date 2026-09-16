package com.car.rental.module.auth.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "认证登录", description = "登录、注册、退出、刷新令牌、找回密码等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final AuthService authService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "登录", description = "公开接口，无需登录。校验用户名密码，返回值包含 accessToken（访问令牌）和 refreshToken（刷新令牌）。全局调试方法：文档右上角 Authorize 按钮填 \"Bearer {accessToken}\"（注意 Bearer 后有空格）后即可调试需登录接口")
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.ok(authService.login(loginDTO));
    }

    @Operation(summary = "注册", description = "公开接口，无需登录。用户名唯一校验，注册成功后可使用用户名密码登录")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.ok("注册成功");
    }

    @Operation(summary = "退出登录", description = "公开接口，无需登录。从请求头 Authorization 中提取 accessToken，连同可选的 refreshToken 一起注销，使其立即失效")
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        String accessToken = extractToken(request);
        String refreshToken = body != null ? body.get("refreshToken") : null;
        authService.logout(accessToken, refreshToken);
        return Result.ok("退出成功");
    }

    /**
     * 无感刷新 access token：前端在 access token 过期前/后用 refresh token 换取新的 access token
     */
    @Operation(summary = "刷新令牌", description = "公开接口，无需登录。用 refreshToken 换取新的 accessToken，实现 access token 无感续期")
    @PostMapping("/refresh")
    public Result<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body != null ? body.get("refreshToken") : null;
        return Result.ok(authService.refreshAccessToken(refreshToken));
    }

    @Operation(summary = "当前登录用户信息", description = "需登录。从令牌中解析当前用户ID，返回用户基本信息")
    @GetMapping("/user/info")
    public Result<?> getUserInfo() {
        Long userId = securityUtil.getCurrentUserId();
        return Result.ok(authService.getUserInfo(userId));
    }

    /** 找回密码 - 身份验证：校验用户名+邮箱，返回临时重置令牌 */
    @Operation(summary = "找回密码-身份验证", description = "公开接口，无需登录。校验用户名与注册邮箱是否匹配，匹配则返回临时重置令牌 resetToken，用于下一步重置密码")
    @PostMapping("/forgot-password/verify")
    public Result<?> forgotPasswordVerify(@Valid @RequestBody ForgotPasswordVerifyDTO dto) {
        return Result.ok(authService.verifyForgotPassword(dto.getUsername(), dto.getEmail()));
    }

    /** 找回密码 - 重置密码：通过临时令牌重置密码 */
    @Operation(summary = "找回密码-重置密码", description = "公开接口，无需登录。凭身份验证步骤返回的临时重置令牌 resetToken 设置新密码，密码至少6个字符")
    @PostMapping("/forgot-password/reset")
    public Result<?> forgotPasswordReset(@Valid @RequestBody ForgotPasswordResetDTO dto) {
        authService.resetPassword(dto.getUsername(), dto.getResetToken(), dto.getNewPassword());
        return Result.ok("密码重置成功");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}