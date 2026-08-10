package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户订单实体（对齐官网下单页面 customer_order 表）
 */
@Data
@TableName("customer_order")
public class CustomerOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 会员ID */
    private Long memberId;

    /** 车辆ID */
    private Long carId;

    /** 车辆名称 */
    private String carName;

    /** 车辆封面图 */
    private String carCover;

    /** 状态：pending/renting/completed/cancelled */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 开始日期 */
    private LocalDate startDate;

    /** 结束日期 */
    private LocalDate endDate;

    /** 天数 */
    private Integer days;

    /** 日租金 */
    private BigDecimal dailyPrice;

    /** 租金总额 */
    private BigDecimal rentAmount;

    /** 优惠券折扣 */
    private BigDecimal couponDiscount;

    /** 使用的优惠券ID */
    private Long couponId;

    /** 用户券记录ID（member_coupon.id） */
    private Long couponUserId;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 城市 */
    private String city;

    /** 门店 */
    private String store;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人电话 */
    private String contactPhone;

    /** 创建时间 */
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDelete;
}
