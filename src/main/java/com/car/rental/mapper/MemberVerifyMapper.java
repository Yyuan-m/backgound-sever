package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.MemberVerifyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 会员实名认证记录 Mapper（跨库访问 car_rental_customer）
 */
@Mapper
public interface MemberVerifyMapper extends BaseMapper<MemberVerifyRecord> {

    /**
     * 分页查询认证记录，JOIN member 表带出会员账号/昵称/手机号/头像/当前认证状态
     *
     * @param status  记录状态筛选：pending/approved/rejected，null 查全部
     * @param keyword 关键字（姓名/身份证号/手机号/昵称模糊匹配）
     */
    @Select("""
            SELECT r.id,
                   r.member_id,
                   r.real_name,
                   r.id_card,
                   r.birth_date,
                   r.driver_license_no,
                   r.driver_license_type,
                   r.driver_license_expire_date,
                   r.id_card_front_img,
                   r.id_card_back_img,
                   r.driver_license_front_img,
                   r.driver_license_back_img,
                   r.status,
                   r.reject_reason,
                   r.reviewer,
                   r.review_time,
                   r.create_time,
                   r.update_time,
                   m.username,
                   m.nickname,
                   m.phone,
                   m.avatar,
                   m.verify_status                       AS member_verify_status
            FROM car_rental_customer.member_verify_record r
            LEFT JOIN car_rental_customer.member m
                ON m.id = r.member_id AND m.is_delete = 0
            WHERE r.is_delete = 0
              AND (#{status} IS NULL OR #{status} = '' OR r.status = #{status})
              AND (#{keyword} IS NULL OR r.real_name LIKE CONCAT('%', #{keyword}, '%')
                                     OR r.id_card LIKE CONCAT('%', #{keyword}, '%')
                                     OR m.phone LIKE CONCAT('%', #{keyword}, '%')
                                     OR m.nickname LIKE CONCAT('%', #{keyword}, '%'))
            ORDER BY (r.status = 'pending') DESC, r.create_time DESC
            """)
    IPage<MemberVerifyRecord> selectVerifyPage(IPage<MemberVerifyRecord> page,
                                                @Param("status") String status,
                                                @Param("keyword") String keyword);

    /** 按记录 ID 查详情（含会员信息） */
    @Select("""
            SELECT r.id,
                   r.member_id,
                   r.real_name,
                   r.id_card,
                   r.birth_date,
                   r.driver_license_no,
                   r.driver_license_type,
                   r.driver_license_expire_date,
                   r.id_card_front_img,
                   r.id_card_back_img,
                   r.driver_license_front_img,
                   r.driver_license_back_img,
                   r.status,
                   r.reject_reason,
                   r.reviewer,
                   r.review_time,
                   r.create_time,
                   r.update_time,
                   m.username,
                   m.nickname,
                   m.phone,
                   m.avatar,
                   m.verify_status                       AS member_verify_status
            FROM car_rental_customer.member_verify_record r
            LEFT JOIN car_rental_customer.member m
                ON m.id = r.member_id AND m.is_delete = 0
            WHERE r.is_delete = 0 AND r.id = #{id}
            """)
    MemberVerifyRecord selectVerifyDetail(@Param("id") Long id);

    /** 审核落库：更新记录状态/驳回原因/审核人/审核时间 */
    @Update("""
            UPDATE car_rental_customer.member_verify_record
            SET status = #{status},
                reject_reason = #{rejectReason},
                reviewer = #{reviewer},
                review_time = NOW()
            WHERE id = #{id} AND is_delete = 0
            """)
    int updateReview(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("rejectReason") String rejectReason,
                     @Param("reviewer") String reviewer);

    /** 审核结果同步到 member 表的认证状态 */
    @Update("""
            UPDATE car_rental_customer.member
            SET verify_status = #{verifyStatus},
                verify_reject_reason = #{rejectReason}
            WHERE id = #{memberId}
            """)
    int updateMemberVerifyStatus(@Param("memberId") Long memberId,
                                 @Param("verifyStatus") String verifyStatus,
                                 @Param("rejectReason") String rejectReason);

    /** 同步 customer_info.real_name_status（0未认证/1已认证/2认证中/3认证失败），存在则更新 */
    @Update("""
            UPDATE customer_info ci
            JOIN car_rental_customer.member m ON m.phone COLLATE utf8mb4_unicode_ci = ci.phone COLLATE utf8mb4_unicode_ci
            SET ci.real_name_status = #{realNameStatus},
                ci.updated_at = NOW()
            WHERE m.id = #{memberId} AND ci.is_delete = 0
            """)
    int updateCustomerInfoRealNameStatus(@Param("memberId") Long memberId,
                                         @Param("realNameStatus") Integer realNameStatus);

    /** 查会员 phone（用于 customer_info 无记录时的 upsert 判断） */
    @Select("""
            SELECT id, phone, real_name, nickname, level, level_name
            FROM car_rental_customer.member
            WHERE id = #{memberId} AND is_delete = 0
            """)
    java.util.Map<String, Object> selectMemberById(@Param("memberId") Long memberId);

    /** 审核统计：各状态数量 + 今日提交数 */
    @Select("""
            SELECT COUNT(*)                                        AS total,
                   COALESCE(SUM(status = 'pending'), 0)            AS pending,
                   COALESCE(SUM(status = 'approved'), 0)            AS approved,
                   COALESCE(SUM(status = 'rejected'), 0)            AS rejected,
                   COALESCE(SUM(DATE(create_time) = CURDATE()), 0)  AS today
            FROM car_rental_customer.member_verify_record
            WHERE is_delete = 0
            """)
    java.util.Map<String, Object> selectVerifyStats();
}
