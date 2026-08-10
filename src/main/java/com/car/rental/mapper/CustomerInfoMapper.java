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
                   COALESCE(ci.real_name_status, 0)             AS real_name_status,
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
                       COALESCE(SUM(rent_amount), 0)            AS total_spent
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
                   COALESCE(ci.real_name_status, 0)             AS real_name_status,
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
                       COALESCE(SUM(rent_amount), 0)            AS total_spent
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
}
