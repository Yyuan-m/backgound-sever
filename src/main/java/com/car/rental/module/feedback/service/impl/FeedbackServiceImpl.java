package com.car.rental.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.Feedback;
import com.car.rental.mapper.FeedbackMapper;
import com.car.rental.module.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    /**
     * 当前登录用户名：JwtTokenFilter 将 username 放在 details（principal 是 userId），
     * 因此优先取 details，取不到再降级用 getName()（即 userId）
     */
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        if (auth.getDetails() instanceof String username && !username.isBlank()) {
            return username;
        }
        return auth.getName();
    }

    @Override
    public IPage<Feedback> getFeedbackList(long pageNum, long pageSize, String type, String status,
                                            String keyword, String startDate, String endDate) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        return feedbackMapper.selectFeedbackPage(page, type, status, keyword, startDate, endDate);
    }

    @Override
    public Map<String, Object> getFeedbackStats() {
        return feedbackMapper.selectFeedbackStats();
    }

    @Override
    public Feedback getFeedbackDetail(Long id) {
        Feedback record = feedbackMapper.selectFeedbackDetail(id);
        if (record == null) {
            throw new BusinessException("记录不存在或已被删除");
        }
        return record;
    }

    @Override
    public void processFeedback(Long id, String remark) {
        if (remark == null || remark.isBlank()) {
            throw new BusinessException("请填写处理备注（沟通结果）");
        }
        Feedback record = feedbackMapper.selectFeedbackDetail(id);
        if (record == null) {
            throw new BusinessException("记录不存在或已被删除");
        }
        if (!"pending".equals(record.getStatus())) {
            throw new BusinessException("该记录已处理，请勿重复操作");
        }

        String handler = currentUsername();
        // 条件更新（status='pending'）兜底防并发重复处理
        int updated = feedbackMapper.processFeedback(id, remark.trim(), handler);
        if (updated == 0) {
            throw new BusinessException("该记录已被处理，请刷新后查看");
        }
    }

    @Override
    public void updateRemark(Long id, String remark) {
        if (remark == null || remark.isBlank()) {
            throw new BusinessException("处理备注不能为空");
        }
        Feedback record = feedbackMapper.selectFeedbackDetail(id);
        if (record == null) {
            throw new BusinessException("记录不存在或已被删除");
        }
        if (!"handled".equals(record.getStatus())) {
            throw new BusinessException("仅已处理记录可修改备注");
        }
        feedbackMapper.updateRemark(id, remark.trim());
    }

    @Override
    public void deleteFeedback(Long id) {
        Feedback record = feedbackMapper.selectFeedbackDetail(id);
        if (record == null) {
            throw new BusinessException("记录不存在或已被删除");
        }
        feedbackMapper.deleteFeedback(id);
    }
}
