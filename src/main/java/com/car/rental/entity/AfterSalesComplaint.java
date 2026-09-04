package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AfterSalesComplaint {
    private Long id;
    private String ticketNo;
    private Long orderId;
    private String orderNo;
    /** 关联会员ID（C端提交时记录） */
    private Long memberId;
    /** 投诉凭证图片（JSON数组字符串，如 ["/uploads/xxx.jpg"]，C端上传） */
    private String images;
    private String customerName;
    private String type;
    private String typeName;
    private String description;
    private String priority;
    private String status;
    private String assignee;
    private String solution;
    private Integer satisfaction;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    /** 关联车辆ID（通过订单反查，非持久化） */
    @TableField(exist = false)
    private Long vehicleId;
    /** 关联车辆名称（来自订单，非持久化） */
    @TableField(exist = false)
    private String vehicleName;
    /** 关联车辆封面（来自订单，非持久化） */
    @TableField(exist = false)
    private String vehicleCover;
    /** 关联车辆车牌号（非持久化） */
    @TableField(exist = false)
    private String plateNumber;
}