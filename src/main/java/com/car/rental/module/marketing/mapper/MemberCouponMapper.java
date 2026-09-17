package com.car.rental.module.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.MemberCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户优惠券 Mapper（操作 car_rental_customer.member_coupon 表）
 */
@Mapper
public interface MemberCouponMapper extends BaseMapper<MemberCoupon> {

    /**
     * 查询用户已领取的券（跨库 JOIN coupon 模板）
     */
    @Select("SELECT mc.*, " +
            "c.name AS coupon_name, c.type AS coupon_type, c.type_name AS coupon_type_name, " +
            "c.value AS coupon_value, c.min_amount AS min_amount, c.discount_cap AS discount_cap, " +
            "c.apply_scope AS apply_scope, c.valid_start_time AS valid_start_time " +
            "FROM car_rental_customer.member_coupon mc " +
            "LEFT JOIN car_rental.coupon c ON mc.coupon_id = c.id AND c.is_delete = 0 " +
            "WHERE mc.member_id = #{memberId} AND mc.is_delete = 0 " +
            "ORDER BY mc.claim_time DESC")
    List<MemberCoupon> selectMyCoupons(@Param("memberId") Long memberId);

    /**
     * 查询某用户某券已领取数量（用于 per_user_limit 校验）
     */
    @Select("SELECT COUNT(*) FROM car_rental_customer.member_coupon " +
            "WHERE member_id = #{memberId} AND coupon_id = #{couponId} AND is_delete = 0")
    int countReceivedByUser(@Param("memberId") Long memberId, @Param("couponId") Long couponId);

    /**
     * 状态流转（带乐观锁版本号校验）
     * @return 影响行数，0 表示版本冲突或状态不符
     */
    @Update("UPDATE car_rental_customer.member_coupon SET status = #{newStatus}, version = version + 1 " +
            "WHERE id = #{id} AND status = #{expectStatus} AND version = #{version} AND is_delete = 0")
    int updateStatusWithVersion(@Param("id") Long id,
                                @Param("expectStatus") String expectStatus,
                                @Param("newStatus") String newStatus,
                                @Param("version") Integer version);

    /**
     * 核销回写订单（带乐观锁）
     */
    @Update("UPDATE car_rental_customer.member_coupon " +
            "SET status = 'used', use_time = NOW(), order_id = #{orderId}, version = version + 1 " +
            "WHERE id = #{id} AND status = 'locked' AND version = #{version} AND is_delete = 0")
    int verifyWithOrder(@Param("id") Long id,
                       @Param("orderId") Long orderId,
                       @Param("version") Integer version);

    /**
     * 校验会员ID列表中真实存在（未删除）的数量（定向发放用）
     */
    @Select("<script>SELECT COUNT(*) FROM car_rental_customer.member " +
            "WHERE is_delete = 0 AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int countExistingMembers(@Param("ids") List<Long> ids);

    /**
     * 查询会员的会员等级（领取"按会员等级"发放的券时校验用）
     */
    @Select("SELECT level FROM car_rental_customer.member WHERE id = #{memberId} AND is_delete = 0")
    String selectMemberLevel(@Param("memberId") Long memberId);

    /**
     * 按用户券记录 ID 查券信息（coupon_id + 券名称/类型），用于订单详情展示优惠券使用信息
     */
    @Select("SELECT mc.id, mc.coupon_id, mc.status, " +
            "c.name AS couponName, c.type_name AS couponTypeName " +
            "FROM car_rental_customer.member_coupon mc " +
            "LEFT JOIN car_rental.coupon c ON c.id = mc.coupon_id " +
            "WHERE mc.id = #{id} LIMIT 1")
    java.util.Map<String, Object> selectCouponInfoById(@Param("id") Long id);

    /**
     * 按月统计优惠券领取数量（claim_time 分月，含全部状态：未使用/锁定/已核销）
     * 用于财务-活动统计 Tab
     */
    @Select("SELECT DATE_FORMAT(claim_time, '%Y-%m') AS month, COUNT(*) AS claimed " +
            "FROM car_rental_customer.member_coupon " +
            "WHERE is_delete = 0 AND claim_time >= DATE_SUB(NOW(), INTERVAL #{months} MONTH) " +
            "GROUP BY DATE_FORMAT(claim_time, '%Y-%m')")
    List<java.util.Map<String, Object>> selectClaimStatsByMonth(@Param("months") int months);

    /**
     * 按月统计优惠券核销数量与优惠金额（use_time 分月，status=used，
     * 金额取关联订单 customer_order.coupon_discount，与财务统计口径一致：仅已完成订单）
     * 用于财务-活动统计 Tab
     */
    @Select("SELECT DATE_FORMAT(mc.use_time, '%Y-%m') AS month, COUNT(*) AS usedCount, " +
            "IFNULL(SUM(o.coupon_discount), 0) AS discountAmount " +
            "FROM car_rental_customer.member_coupon mc " +
            "JOIN car_rental.customer_order o ON o.id = mc.order_id AND o.is_delete = 0 " +
            "WHERE mc.status = 'used' AND mc.is_delete = 0 " +
            "AND mc.use_time >= DATE_SUB(NOW(), INTERVAL #{months} MONTH) " +
            "GROUP BY DATE_FORMAT(mc.use_time, '%Y-%m')")
    List<java.util.Map<String, Object>> selectUsageStatsByMonth(@Param("months") int months);

    /**
     * 全量统计优惠券累计领取数量（不限时间，含全部状态），用于活动统计汇总卡片
     */
    @Select("SELECT COUNT(*) AS totalClaimed " +
            "FROM car_rental_customer.member_coupon " +
            "WHERE is_delete = 0")
    Long selectClaimTotal();

    /**
     * 全量统计优惠券累计核销数量与优惠金额（不限时间，status=used，
     * 金额取关联订单 coupon_discount，仅已完成订单），用于活动统计汇总卡片
     */
    @Select("SELECT COUNT(*) AS totalUsed, " +
            "IFNULL(SUM(o.coupon_discount), 0) AS totalDiscountAmount " +
            "FROM car_rental_customer.member_coupon mc " +
            "JOIN car_rental.customer_order o ON o.id = mc.order_id AND o.is_delete = 0 " +
            "WHERE mc.status = 'used' AND mc.is_delete = 0")
    java.util.Map<String, Object> selectUsageTotal();
}
