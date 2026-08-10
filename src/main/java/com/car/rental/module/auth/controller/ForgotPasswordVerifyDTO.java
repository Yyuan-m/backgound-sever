package com.car.rental.module.auth.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordVerifyDTO {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "请输入注册邮箱")
    @Email(message = "请输入正确的邮箱格式")
    private String email;
}
