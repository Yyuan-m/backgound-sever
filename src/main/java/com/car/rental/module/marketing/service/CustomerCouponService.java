package com.car.rental.module.marketing.service;

import com.car.rental.entity.Coupon;
import com.car.rental.entity.MemberCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * C端优惠券服务（供 customer-client 调用）
 * 涉及 car_rental.coupon（券模板）与 car_rental_customer.member_coupon（领取实例）跨库操作
 */
public interface CustomerCouponService {

    /** 可领券列表（已投放 + 有效期内 + 有库存） */
    List<Coupon> listAvailable();

    /** 券详情 */
    Coupon getCouponDetail(Long id);

    /** 我的券（可按状态筛选，跨库 JOIN coupon 模板） */
    List<MemberCoupon> listMine(Long memberId, String status);

    /** 下单可用券（某用户在某车某金额下可用的未使用券） */
    List<MemberCoupon> listUsable(Long memberId, Long carId, BigDecimal amount);

    /** 领取优惠券（原子扣库存 + 写 member_coupon） */
    Long receive(Long memberId, Long couponId, String source);

    /** 锁定优惠券（下单预占，unused → locked） */
    void lock(Long memberCouponId, Long memberId);

    /** 取消锁定（订单创建失败/取消时，locked → unused） */
    void cancelLock(Long memberCouponId, Long memberId);

    /** 核销（订单完成时，locked → used，回写 order_id，coupon.used_count++） */
    void verify(Long memberCouponId, Long memberId, Long orderId);

    /** 计算优惠金额（不实际核销，仅用于下单预览） */
    BigDecimal calculateDiscount(Long couponId, BigDecimal amount);
}
