package com.car.rental.module.auth.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordResetDTO {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "重置令牌无效")
    private String resetToken;

    @NotBlank(message = "请输入新密码")
    @Size(min = 6, message = "密码至少6个字符")
    private String newPassword;
}
