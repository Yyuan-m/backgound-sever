package com.car.rental.module.auth.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.module.auth.service.AuthService;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final AuthService authService;
    private final SecurityUtil securityUtil;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.ok(authService.login(loginDTO));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.ok("注册成功");
    }

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
    @PostMapping("/refresh")
    public Result<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body != null ? body.get("refreshToken") : null;
        return Result.ok(authService.refreshAccessToken(refreshToken));
    }

    @GetMapping("/user/info")
    public Result<?> getUserInfo() {
        Long userId = securityUtil.getCurrentUserId();
        return Result.ok(authService.getUserInfo(userId));
    }

    /** 找回密码 - 身份验证：校验用户名+邮箱，返回临时重置令牌 */
    @PostMapping("/forgot-password/verify")
    public Result<?> forgotPasswordVerify(@Valid @RequestBody ForgotPasswordVerifyDTO dto) {
        return Result.ok(authService.verifyForgotPassword(dto.getUsername(), dto.getEmail()));
    }

    /** 找回密码 - 重置密码：通过临时令牌重置密码 */
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