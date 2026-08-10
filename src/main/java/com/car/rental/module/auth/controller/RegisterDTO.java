package com.car.rental.module.auth.controller;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterDTO {
    @NotBlank private String username;
    @NotBlank private String password;
    private String name;
    private String email;
    private String phone;
}