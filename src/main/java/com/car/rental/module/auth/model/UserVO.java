package com.car.rental.module.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String roleName;
    private List<String> roles;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTime;
    private List<String> permissions;
}