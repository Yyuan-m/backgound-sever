package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("finance_record")
public class FinanceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    private String typeName;

    private String orderNo;

    private String customerName;

    private BigDecimal amount;

    private String method;

    private String status;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}