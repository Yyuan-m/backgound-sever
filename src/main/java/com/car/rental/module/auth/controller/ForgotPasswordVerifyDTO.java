package com.car.rental.module.auth.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "找回密码-身份验证请求参数")
public class ForgotPasswordVerifyDTO {

    @NotBlank(message = "请输入用户名")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "请输入注册邮箱")
    @Email(message = "请输入正确的邮箱格式")
    @Schema(description = "注册邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
