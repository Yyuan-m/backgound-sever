package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 优惠券-车辆关联表（一张券可关联多辆车，一对多）
 */
@Data
@TableName("coupon_car")
public class CouponCar {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long couponId;

    private Long carId;

    private LocalDateTime createdAt;
}
