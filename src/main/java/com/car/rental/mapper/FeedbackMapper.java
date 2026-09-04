package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Feedback;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * 预约咨询/留言反馈 Mapper（跨库访问 car_rental_customer.feedback）
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    /**
     * 分页查询咨询/反馈记录，LEFT JOIN member 带出会员账号/昵称/头像
     *
     * @param type      类型筛选：appointment/feedback，null 查全部
     * @param status    状态筛选：pending/handled，null 查全部
     * @param keyword   关键字（姓名/手机号/留言内容/意向车型模糊匹配）
     * @param startDate 提交时间起（yyyy-MM-dd，含当天）
     * @param endDate   提交时间止（yyyy-MM-dd，含当天）
     */
    @Select("""
            SELECT f.id,
                   f.member_id,
                   f.type,
                   f.name,
                   f.phone,
                   f.content,
                   f.car_type,
                   f.rent_date,
                   f.status,
                   f.remark,
                   f.handler,
                   f.process_time,
                   f.update_time,
                   f.create_time,
                   m.username,
                   m.nickname,
                   m.avatar
            FROM car_rental_customer.feedback f
            LEFT JOIN car_rental_customer.member m
                ON m.id = f.member_id AND m.is_delete = 0
            WHERE (#{type} IS NULL OR #{type} = '' OR f.type = #{type})
              AND (#{status} IS NULL OR #{status} = '' OR f.status = #{status})
              AND (#{keyword} IS NULL OR f.name LIKE CONCAT('%', #{keyword}, '%')
                                     OR f.phone LIKE CONCAT('%', #{keyword}, '%')
                                     OR f.content LIKE CONCAT('%', #{keyword}, '%')
                                     OR f.car_type LIKE CONCAT('%', #{keyword}, '%'))
              AND (#{startDate} IS NULL OR f.create_time >= #{startDate})
              AND (#{endDate} IS NULL OR f.create_time < DATE_ADD(#{endDate}, INTERVAL 1 DAY))
            ORDER BY (f.status = 'pending') DESC, f.create_time DESC
            """)
    IPage<Feedback> selectFeedbackPage(IPage<Feedback> page,
                                        @Param("type") String type,
                                        @Param("status") String status,
                                        @Param("keyword") String keyword,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate);

    /** 按记录 ID 查详情（含会员信息） */
    @Select("""
            SELECT f.id,
                   f.member_id,
                   f.type,
                   f.name,
                   f.phone,
                   f.content,
                   f.car_type,
                   f.rent_date,
                   f.status,
                   f.remark,
                   f.handler,
                   f.process_time,
                   f.update_time,
                   f.create_time,
                   m.username,
                   m.nickname,
                   m.avatar
            FROM car_rental_customer.feedback f
            LEFT JOIN car_rental_customer.member m
                ON m.id = f.member_id AND m.is_delete = 0
            WHERE f.id = #{id}
            """)
    Feedback selectFeedbackDetail(@Param("id") Long id);

    /** 处理落库：仅待处理记录可标记已处理（乐观条件更新，防重复处理） */
    @Update("""
            UPDATE car_rental_customer.feedback
            SET status = 'handled',
                remark = #{remark},
                handler = #{handler},
                process_time = NOW(),
                update_time = NOW()
            WHERE id = #{id} AND status = 'pending'
            """)
    int processFeedback(@Param("id") Long id,
                        @Param("remark") String remark,
                        @Param("handler") String handler);

    /** 已处理记录补充/修改处理备注 */
    @Update("""
            UPDATE car_rental_customer.feedback
            SET remark = #{remark},
                update_time = NOW()
            WHERE id = #{id} AND status = 'handled'
            """)
    int updateRemark(@Param("id") Long id, @Param("remark") String remark);

    /** 物理删除（feedback 表无逻辑删除字段） */
    @Delete("DELETE FROM car_rental_customer.feedback WHERE id = #{id}")
    int deleteFeedback(@Param("id") Long id);

    /** 统计：各状态数量 + 今日新增 + 未来7天取车的待处理预约 + 类型分布 */
    @Select("""
            SELECT COUNT(*) AS total,
                   COALESCE(SUM(status = 'pending'), 0)  AS pending,
                   COALESCE(SUM(status = 'handled'), 0)  AS handled,
                   COALESCE(SUM(DATE(create_time) = CURDATE()), 0) AS today,
                   COALESCE(SUM(status = 'pending' AND rent_date IS NOT NULL
                       AND rent_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)), 0) AS upcoming,
                   COALESCE(SUM(type = 'appointment'), 0) AS appointment,
                   COALESCE(SUM(type = 'feedback'), 0)     AS feedbackCount,
                   COALESCE(SUM(status = 'pending' AND type = 'appointment'), 0) AS pendingAppointment,
                   COALESCE(SUM(status = 'pending' AND type = 'feedback'), 0)     AS pendingFeedback
            FROM car_rental_customer.feedback
            """)
    Map<String, Object> selectFeedbackStats();
}
