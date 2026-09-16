package com.car.rental.module.auth.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "找回密码-重置密码请求参数")
public class ForgotPasswordResetDTO {

    @NotBlank(message = "请输入用户名")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "重置令牌无效")
    @Schema(description = "临时重置令牌（身份验证步骤返回）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resetToken;

    @NotBlank(message = "请输入新密码")
    @Size(min = 6, message = "密码至少6个字符")
    @Schema(description = "新密码（至少6个字符）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
