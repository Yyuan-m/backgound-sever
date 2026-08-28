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
 * 订单车辆明细（一单可含多车）
 */
@Data
@TableName("customer_order_item")
public class CustomerOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联主订单ID customer_order.id */
    private Long orderId;

    /** 车辆ID car_info.id */
    private Long carId;

    /** 车辆名称快照 */
    private String carName;

    /** 车辆封面快照 */
    private String carCover;

    /** 租期开始日 */
    private LocalDate startDate;

    /** 租期结束日 */
    private LocalDate endDate;

    /** 租赁天数 */
    private Integer days;

    /** 日租金快照 */
    private BigDecimal dailyPrice;

    /** 该车租金小计(折扣前) */
    private BigDecimal rentAmount;

    /** 该车分摊优惠金额 */
    private BigDecimal discountAmount;

    /** 该车券后小计(应付) */
    private BigDecimal totalAmount;

    /** 创建时间 */
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDelete;
}