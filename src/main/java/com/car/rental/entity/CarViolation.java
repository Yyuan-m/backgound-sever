package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarViolation {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private Long orderId;
    /** 关联订单号（非数据库字段，由 Service 层关联查询填充） */
    @TableField(exist = false)
    private String orderNo;
    /** 关联客户姓名（非数据库字段，由 Service 层关联查询填充） */
    @TableField(exist = false)
    private String customerName;
    private String violationType;
    private LocalDateTime violationDate;
    private String location;
    private BigDecimal fineAmount;
    private Integer points;
    private String status;
    private String handler;
    private LocalDateTime handleDate;
    private String remark;
    private LocalDateTime createdAt;
}