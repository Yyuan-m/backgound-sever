package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reconciliation")
public class Reconciliation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate date;

    private BigDecimal rentalIncome;

    private BigDecimal fees;

    private BigDecimal netIncome;

    private String status;

    private String checkedBy;

    private LocalDateTime checkedAt;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
