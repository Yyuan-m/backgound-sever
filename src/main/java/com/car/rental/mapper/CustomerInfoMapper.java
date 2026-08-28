package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CustomerInfoMapper extends BaseMapper<CustomerInfo> {

    /**
     * 跨库分页查询：以 car_rental_customer.member 为主表，LEFT JOIN customer_info
     * 通过 phone 字段关联，将官网会员表 member 的字段作为主信息，
     * customer_info 表的特有字段（is_blacklist / tags / discount / real_name_status）作为补充
     *
     * @param page    分页参数（由 MyBatis-Plus 分页插件自动处理）
     * @param keyword 关键字（用户名/昵称/真实姓名/手机号模糊匹配）
     * @param status  会员状态（member.status）
     * @param level   会员等级（member.level）
     */
    @Select("""
            SELECT m.id                                          AS member_id,
                   m.id                                          AS id,
                   m.username,
                   m.nickname,
                   m.real_name                                  AS name,
                   m.phone,
                   m.gender,
                   m.birthday,
                   m.email,
                   m.avatar,
                   m.id_card,
                   m.id_card_front_img,
                   m.id_card_back_img,
                   m.driver_license_no                          AS driver_license,
                   m.driver_license_type,
                   m.driver_license_front_img,
                   m.driver_license_back_img,
                   m.driver_license_expire_date                 AS driver_license_expire,
                   m.province,
                   m.city,
                   m.address,
                   m.level                                      AS membership_level,
                   m.level_name                                 AS membership_name,
                   m.credit_score,
                   COALESCE(o.total_orders, 0)                  AS total_orders,
                   COALESCE(o.total_spent, 0)                   AS total_spent,
                   m.last_login_time,
                   m.last_login_ip,
                   m.status,
                   m.create_time                                AS created_at,
                   m.update_time                                AS updated_at,
                   -- customer_info 特有字段，JOIN 不到时使用默认值
                   ci.id                                        AS customer_id,
                   CASE m.verify_status WHEN 'verified' THEN 1 WHEN 'pending' THEN 2 WHEN 'rejected' THEN 3 ELSE COALESCE(ci.real_name_status, 0) END AS real_name_status,
                   COALESCE(ci.discount, 1)                     AS discount,
                   ci.tags                                      AS tags,
                   COALESCE(ci.is_blacklist, 0)                 AS is_blacklist
            FROM car_rental_customer.member m
            LEFT JOIN customer_info ci
                ON ci.phone COLLATE utf8mb4_unicode_ci = m.phone COLLATE utf8mb4_unicode_ci
                AND ci.is_delete = 0
            LEFT JOIN (
                SELECT member_id,
                       COUNT(*)                                 AS total_orders,
                       COALESCE(SUM(total_amount), 0)           AS total_spent
                FROM customer_order
                WHERE status = 'completed' AND is_delete = 0
                GROUP BY member_id
            ) o ON o.member_id = m.id
            WHERE m.is_delete = 0
              AND (#{keyword} IS NULL OR m.username LIKE CONCAT('%', #{keyword}, '%')
                                         OR m.nickname LIKE CONCAT('%', #{keyword}, '%')
                                         OR m.real_name LIKE CONCAT('%', #{keyword}, '%')
                                         OR m.phone LIKE CONCAT('%', #{keyword}, '%'))
              AND (#{status} IS NULL OR m.status = #{status})
              AND (#{level} IS NULL OR #{level} = '' OR m.level = #{level})
            ORDER BY m.create_time DESC
            """)
    IPage<CustomerInfo> selectPageWithMember(IPage<CustomerInfo> page,
                                              @Param("keyword") String keyword,
                                              @Param("status") Integer status,
                                              @Param("level") String level);

    /**
     * 按 member.id 查询详情（跨库 LEFT JOIN customer_info）
     */
    @Select("""
            SELECT m.id                                          AS member_id,
                   m.id                                          AS id,
                   m.username,
                   m.nickname,
                   m.real_name                                  AS name,
                   m.phone,
                   m.gender,
                   m.birthday,
                   m.email,
                   m.avatar,
                   m.id_card,
                   m.id_card_front_img,
                   m.id_card_back_img,
                   m.driver_license_no                          AS driver_license,
                   m.driver_license_type,
                   m.driver_license_front_img,
                   m.driver_license_back_img,
                   m.driver_license_expire_date                 AS driver_license_expire,
                   m.province,
                   m.city,
                   m.address,
                   m.level                                      AS membership_level,
                   m.level_name                                 AS membership_name,
                   m.credit_score,
                   COALESCE(o.total_orders, 0)                  AS total_orders,
                   COALESCE(o.total_spent, 0)                   AS total_spent,
                   m.last_login_time,
                   m.last_login_ip,
                   m.status,
                   m.create_time                                AS created_at,
                   m.update_time                                AS updated_at,
                   ci.id                                        AS customer_id,
                   CASE m.verify_status WHEN 'verified' THEN 1 WHEN 'pending' THEN 2 WHEN 'rejected' THEN 3 ELSE COALESCE(ci.real_name_status, 0) END AS real_name_status,
                   COALESCE(ci.discount, 1)                     AS discount,
                   ci.tags                                      AS tags,
                   COALESCE(ci.is_blacklist, 0)                 AS is_blacklist
            FROM car_rental_customer.member m
            LEFT JOIN customer_info ci
                ON ci.phone COLLATE utf8mb4_unicode_ci = m.phone COLLATE utf8mb4_unicode_ci
                AND ci.is_delete = 0
            LEFT JOIN (
                SELECT member_id,
                       COUNT(*)                                 AS total_orders,
                       COALESCE(SUM(total_amount), 0)           AS total_spent
                FROM customer_order
                WHERE status = 'completed' AND is_delete = 0
                GROUP BY member_id
            ) o ON o.member_id = m.id
            WHERE m.is_delete = 0 AND m.id = #{memberId}
            """)
    CustomerInfo selectDetailByMemberId(@Param("memberId") Long memberId);

    /**
     * 通过手机号查询 member 表（用于 upsert customer_info 时获取 member 信息）
     */
    @Select("""
            SELECT id, phone, real_name, nickname, level, level_name
            FROM car_rental_customer.member
            WHERE id = #{memberId} AND is_delete = 0
            """)
    java.util.Map<String, Object> selectMemberById(@Param("memberId") Long memberId);

    /**
     * 更新 car_rental_customer.member 表的 status 字段（用于后台禁用/启用同步到官网）
     */
    @Update("UPDATE car_rental_customer.member SET status = #{status} WHERE id = #{memberId}")
    int updateMemberStatus(@Param("memberId") Long memberId, @Param("status") Integer status);

    /**
     * 查询 member 表的身份证信息（用于判断是否满足实名条件）
     */
    @Select("""
            SELECT id_card, id_card_front_img, id_card_back_img
            FROM car_rental_customer.member
            WHERE id = #{memberId} AND is_delete = 0
            """)
    java.util.Map<String, Object> selectMemberIdCardInfo(@Param("memberId") Long memberId);

    /**
     * 重算并更新会员等级（跨库，订单完成时调用）。
     * 规则（累计订单数与累计消费两个维度取较高等级）：
     *   普通会员：无已完成订单且消费为 0
     *   银卡会员：0 < 订单数 <= 10 或 0 < 消费 <= 10000
     *   金卡会员：10 < 订单数 <= 50 或 10000 < 消费 <= 50000
     *   钻石会员：50 < 订单数 <= 500 或 50000 < 消费 <= 500000
     *   黑卡会员：订单数 > 500 或消费 > 500000
     * 统计口径：status='completed' 且 is_delete=0 的 customer_order，
     * 消费金额为 SUM(total_amount)（实付净额，已扣优惠券）。
     */
    @Update("""
            UPDATE car_rental_customer.member m
            LEFT JOIN (
                SELECT member_id,
                       COUNT(*)                            AS order_cnt,
                       COALESCE(SUM(total_amount), 0)      AS total_spent
                FROM car_rental.customer_order
                WHERE status = 'completed' AND is_delete = 0
                GROUP BY member_id
            ) s ON s.member_id = m.id
            SET m.level = CASE
                    WHEN COALESCE(s.order_cnt, 0) > 500 OR COALESCE(s.total_spent, 0) > 500000 THEN 'black'
                    WHEN (COALESCE(s.order_cnt, 0) > 50 AND COALESCE(s.order_cnt, 0) <= 500)
                      OR (COALESCE(s.total_spent, 0) > 50000 AND COALESCE(s.total_spent, 0) <= 500000) THEN 'diamond'
                    WHEN (COALESCE(s.order_cnt, 0) > 10 AND COALESCE(s.order_cnt, 0) <= 50)
                      OR (COALESCE(s.total_spent, 0) > 10000 AND COALESCE(s.total_spent, 0) <= 50000) THEN 'gold'
                    WHEN (COALESCE(s.order_cnt, 0) > 0 AND COALESCE(s.order_cnt, 0) <= 10)
                      OR (COALESCE(s.total_spent, 0) > 0 AND COALESCE(s.total_spent, 0) <= 10000) THEN 'silver'
                    ELSE 'normal'
                END,
                m.level_name = CASE
                    WHEN COALESCE(s.order_cnt, 0) > 500 OR COALESCE(s.total_spent, 0) > 500000 THEN '黑卡会员'
                    WHEN (COALESCE(s.order_cnt, 0) > 50 AND COALESCE(s.order_cnt, 0) <= 500)
                      OR (COALESCE(s.total_spent, 0) > 50000 AND COALESCE(s.total_spent, 0) <= 500000) THEN '钻石会员'
                    WHEN (COALESCE(s.order_cnt, 0) > 10 AND COALESCE(s.order_cnt, 0) <= 50)
                      OR (COALESCE(s.total_spent, 0) > 10000 AND COALESCE(s.total_spent, 0) <= 50000) THEN '金卡会员'
                    WHEN (COALESCE(s.order_cnt, 0) > 0 AND COALESCE(s.order_cnt, 0) <= 10)
                      OR (COALESCE(s.total_spent, 0) > 0 AND COALESCE(s.total_spent, 0) <= 10000) THEN '银卡会员'
                    ELSE '普通会员'
                END
            WHERE m.id = #{memberId}
            """)
    int recalcMemberLevel(@Param("memberId") Long memberId);
}
