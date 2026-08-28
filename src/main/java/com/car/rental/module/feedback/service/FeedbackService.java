package com.car.rental.module.feedback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Feedback;

import java.util.Map;

/**
 * 预约咨询/留言反馈处理服务
 */
public interface FeedbackService {

    /** 分页查询（type: appointment/feedback；status: pending/handled；关键字+提交时间范围） */
    IPage<Feedback> getFeedbackList(long pageNum, long pageSize, String type, String status,
                                     String keyword, String startDate, String endDate);

    /** 统计概览：total/pending/handled/today/upcoming/appointment/feedbackCount */
    Map<String, Object> getFeedbackStats();

    /** 记录详情（含会员信息） */
    Feedback getFeedbackDetail(Long id);

    /** 标记已处理：记录处理备注/处理人/处理时间（仅待处理可操作，防重复） */
    void processFeedback(Long id, String remark);

    /** 已处理记录补充/修改处理备注 */
    void updateRemark(Long id, String remark);

    /** 删除记录（物理删除，用于清理垃圾数据） */
    void deleteFeedback(Long id);
}
