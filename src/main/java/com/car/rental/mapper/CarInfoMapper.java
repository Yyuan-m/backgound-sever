package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CarInfoMapper extends BaseMapper<CarInfo> {

    /**
     * 车辆分页查询（实时状态版）：
     * 通过 LEFT JOIN 子查询统计每辆车"当天正在租赁"的订单数，
     * 订单条件：状态为待支付(pending)/租赁中(renting)，且当天落在租期内
     * （start_date <= 当天 <= end_date，end_date 当天仍视为在租，与订单自动完成规则一致）。
     * 当天在租的车辆实时状态显示为 'rented'（租赁中），
     * 否则回退到 car_info.status（idle 空闲 / offline 已下架）。
     *
     * 预约未来日期的订单（如本月 28-30 号）不算当天在租：
     * 今天到 27 号之间车辆仍显示空闲、可继续出租，28 号起才显示租赁中。
     * 同时统计未来预约数（pending/renting 且 start_date > 今天）→ reserved_count，
     * 用于列表"是否已预约"展示。
     *
     * 采用实时聚合而非订单流转时回写 car_info.status，避免状态同步遗漏
     * （历史订单数据无需修复即可正确显示），与 CustomerInfoMapper 的实时聚合方案保持一致。
     *
     * 注意：原生 SQL 需手动追加 is_delete = 0 条件（@TableLogic 不生效）
     *
     * @param page    分页参数（由 MyBatis-Plus 分页插件自动处理）
     * @param keyword 关键字（车名/品牌/车牌号模糊匹配）
     * @param type    车型
     * @param status  车辆状态（idle / rented / offline，基于实时计算结果过滤）
     */
    @Select("""
            SELECT c.id, c.name, c.brand, c.series, c.type, c.plate_number, c.vin, c.engine_no,
                   c.registration_date, c.mileage, c.displacement, c.seats, c.color,
                   c.condition_level, c.original_value, c.residual_value,
                   CASE WHEN COALESCE(ao.active_orders, 0) > 0 THEN 'rented' ELSE c.status END AS status,
                   COALESCE(ro.reserved_orders, 0) AS reserved_count,
                   c.daily_price, c.daily_cost, c.min_rent_days, c.max_rent_days, c.half_day_price, c.night_price,
                   c.weekly_discount, c.monthly_discount, c.holiday_surcharge, c.overtime_per_hour,
                   c.remote_return_fee, c.images, c.description, c.tags, c.is_hot, c.is_recommend,
                   c.is_delete, c.created_at, c.updated_at
            FROM car_info c
            LEFT JOIN (
                SELECT car_id, COUNT(*) AS active_orders
                FROM customer_order
                WHERE status IN ('pending', 'renting') AND is_delete = 0
                  AND start_date <= CURDATE() AND end_date >= CURDATE()
                GROUP BY car_id
            ) ao ON ao.car_id = c.id
            LEFT JOIN (
                SELECT car_id, COUNT(*) AS reserved_orders
                FROM customer_order
                WHERE status IN ('pending', 'renting') AND is_delete = 0
                  AND start_date > CURDATE()
                GROUP BY car_id
            ) ro ON ro.car_id = c.id
            WHERE c.is_delete = 0
              AND (#{keyword} IS NULL OR c.name LIKE CONCAT('%', #{keyword}, '%')
                                         OR c.brand LIKE CONCAT('%', #{keyword}, '%')
                                         OR c.plate_number LIKE CONCAT('%', #{keyword}, '%'))
              AND (#{type} IS NULL OR c.type = #{type})
              AND (#{status} IS NULL OR #{status} = ''
                   OR (CASE WHEN COALESCE(ao.active_orders, 0) > 0 THEN 'rented' ELSE c.status END) = #{status})
            ORDER BY c.created_at DESC
            """)
    IPage<CarInfo> selectPageWithRealtimeStatus(IPage<CarInfo> page,
                                                 @Param("keyword") String keyword,
                                                 @Param("type") String type,
                                                 @Param("status") String status);
}
