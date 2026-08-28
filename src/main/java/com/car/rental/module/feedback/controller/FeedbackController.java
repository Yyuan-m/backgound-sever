package com.car.rental.module.feedback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Feedback;
import com.car.rental.module.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 预约咨询/留言反馈管理（数据来自 car_rental_customer.feedback，C端提交）
 *
 * 处理流程：待处理置顶查看 → 联系客户（姓名/手机/意向车型/取车日期）
 *         → 标记已处理（必填沟通备注，记录处理人/时间）→ 已处理可补充备注
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 记录列表（type: appointment预约咨询/feedback留言反馈；status: pending/handled）
     * 支持关键字（姓名/手机号/内容/意向车型）与提交时间范围筛选，待处理置顶
     */
    @GetMapping("/list")
    @RequirePermission("feedback:list")
    public Result<PageResult<Feedback>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        IPage<Feedback> page = feedbackService.getFeedbackList(
                pageNum, pageSize, type, status, keyword, startDate, endDate);
        return Result.ok(PageResult.of(page));
    }

    /** 统计概览：total/pending/handled/today/upcoming/appointment/feedbackCount */
    @GetMapping("/stats")
    @RequirePermission("feedback:list")
    public Result<java.util.Map<String, Object>> stats() {
        return Result.ok(feedbackService.getFeedbackStats());
    }

    /** 记录详情（含关联会员信息） */
    @GetMapping("/detail/{id}")
    @RequirePermission("feedback:list")
    public Result<Feedback> detail(@PathVariable Long id) {
        return Result.ok(feedbackService.getFeedbackDetail(id));
    }

    /** 标记已处理（必填处理备注：沟通结果；防重复处理） */
    @PostMapping("/process/{id}")
    @RequirePermission("feedback:update")
    public Result<Void> process(@PathVariable Long id, @Valid @RequestBody FeedbackProcessDTO dto) {
        feedbackService.processFeedback(id, dto.getRemark());
        return Result.ok();
    }

    /** 修改处理备注（仅已处理记录） */
    @PutMapping("/remark/{id}")
    @RequirePermission("feedback:update")
    public Result<Void> updateRemark(@PathVariable Long id, @Valid @RequestBody FeedbackProcessDTO dto) {
        feedbackService.updateRemark(id, dto.getRemark());
        return Result.ok();
    }

    /** 删除记录（物理删除，用于清理垃圾数据） */
    @DeleteMapping("/delete/{id}")
    @RequirePermission("feedback:update")
    public Result<Void> delete(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return Result.ok();
    }
}
