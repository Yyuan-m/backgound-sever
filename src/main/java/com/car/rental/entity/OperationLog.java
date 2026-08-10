package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String action;
    private String description;
    private String operator;
    private String ip;
    private Integer status;
    private LocalDateTime createdAt;

    /** 操作人用户名（非数据库字段，由 Service 层关联 sys_user 表查询填充） */
    @TableField(exist = false)
    private String operatorName;
}