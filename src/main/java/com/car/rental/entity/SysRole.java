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
@TableName(value = "sys_role", autoResultMap = true)
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String roleKey;

    private Integer status;

    private String description;

    @TableField(value = "menu_permissions", typeHandler = JacksonTypeHandler.class)
    private List<String> menuPermissions;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}