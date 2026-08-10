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
@TableName("cost_record")
public class CostRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    private String typeName;

    private String detail;

    private BigDecimal amount;

    private LocalDate date;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
