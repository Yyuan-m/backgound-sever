package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "sys_user", autoResultMap = true)
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private String role;

    private String roleName;

    @TableField(value = "roles", typeHandler = JacksonTypeHandler.class)
    private List<String> roles;

    private Integer status;

    private String lastLoginIp;

    private LocalDateTime lastLoginTime;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}