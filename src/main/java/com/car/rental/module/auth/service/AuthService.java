package com.car.rental.module.auth.service;

import com.car.rental.module.auth.controller.LoginDTO;
import com.car.rental.module.auth.controller.RegisterDTO;
import com.car.rental.module.auth.model.UserVO;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    void logout(String accessToken, String refreshToken);

    UserVO getUserInfo(Long userId);

    /**
     * 使用 refresh token 刷新 access token（无感刷新）
     * @param refreshToken 前端存储的长有效期 refresh token
     * @return 包含新的 access token（以及滚动后的 refresh token）
     */
    Map<String, Object> refreshAccessToken(String refreshToken);

    /** 找回密码 - 身份验证：校验用户名+邮箱，返回临时重置令牌 */
    Map<String, Object> verifyForgotPassword(String username, String email);

    /** 找回密码 - 重置密码：通过临时令牌重置密码 */
    void resetPassword(String username, String resetToken, String newPassword);
}
