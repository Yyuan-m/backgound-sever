package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CustomerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder> {

    /**
     * 按状态统计订单数量（全量，不含已删除订单）
     * 使用原生 SQL 避免 MyBatis-Plus QueryWrapper 在 selectMaps + 聚合函数场景下的解析差异
     */
    @Select("SELECT status AS status, COUNT(*) AS cnt FROM customer_order WHERE is_delete = 0 GROUP BY status")
    List<Map<String, Object>> selectStatusCount();

    /**
     * 客户租车次数 Top N（仅统计已完成订单）
     * 订单 member_id 关联官网会员表 car_rental_customer.member（跨库），姓名取租客的真实姓名字段
     */
    @Select("SELECT COALESCE(NULLIF(m.real_name, ''), NULLIF(m.nickname, ''), m.username) AS name, COUNT(*) AS value " +
            "FROM customer_order co " +
            "JOIN car_rental_customer.member m ON m.id = co.member_id AND m.is_delete = 0 " +
            "WHERE co.status = 'completed' AND co.is_delete = 0 " +
            "GROUP BY co.member_id, m.real_name, m.nickname, m.username " +
            "ORDER BY value DESC LIMIT #{limit}")
    List<Map<String, Object>> selectCustomerRentalTopN(@Param("limit") int limit);

    /**
     * 优惠券使用统计：以 coupon 表为主全量返回每张券（含未使用的），
     * 统计使用次数与优惠总金额（仅已完成订单且实际产生优惠）
     * 返回券的 type（discount/deduction/duration）与 status（draft/published/offline）供前端筛选
     */
    @Select("SELECT c.id, c.name, c.type, c.status, " +
            "COALESCE(o.used_count, 0) AS usedCount, " +
            "COALESCE(o.discount_total, 0) AS discountTotal " +
            "FROM coupon c " +
            "LEFT JOIN (" +
            "  SELECT coupon_id, COUNT(*) AS used_count, SUM(coupon_discount) AS discount_total " +
            "  FROM customer_order " +
            "  WHERE status = 'completed' AND is_delete = 0 AND coupon_discount > 0 AND coupon_id IS NOT NULL " +
            "  GROUP BY coupon_id" +
            ") o ON o.coupon_id = c.id " +
            "WHERE c.is_delete = 0 " +
            "ORDER BY usedCount DESC, c.id ASC")
    List<Map<String, Object>> selectCouponUsage();

    /**
     * 当前在租车辆数（status=renting 订单的明细车辆 ∪ 主表 carId，去重）
     */
    @Select("SELECT COUNT(DISTINCT car_id) FROM (" +
            "SELECT coi.car_id FROM customer_order co " +
            "JOIN customer_order_item coi ON coi.order_id = co.id AND coi.is_delete = 0 " +
            "WHERE co.status = 'renting' AND co.is_delete = 0 " +
            "UNION " +
            "SELECT co.car_id FROM customer_order co " +
            "WHERE co.status = 'renting' AND co.is_delete = 0 AND co.car_id IS NOT NULL" +
            ") t")
    long countRentingVehicles();
}
