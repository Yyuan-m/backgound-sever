package com.car.rental.module.feedback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Feedback;
import com.car.rental.module.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 预约咨询/留言反馈管理（数据来自 car_rental_customer.feedback，C端提交）
 *
 * 处理流程：待处理置顶查看 → 联系客户（姓名/手机/意向车型/取车日期）
 *         → 标记已处理（必填沟通备注，记录处理人/时间）→ 已处理可补充备注
 */
@Tag(name = "反馈管理", description = "预约咨询/留言反馈记录的查询、统计、处理与删除；数据来自 C 端提交（car_rental_customer.feedback）")
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 记录列表（type: appointment预约咨询/feedback留言反馈；status: pending/handled）
     * 支持关键字（姓名/手机号/内容/意向车型）与提交时间范围筛选，待处理置顶
     */
    @Operation(summary = "反馈记录列表（分页）", description = "类型 appointment 预约咨询 / feedback 留言反馈；状态 pending 待处理 / handled 已处理；支持关键字（姓名/手机号/内容/意向车型）与提交时间范围筛选，待处理置顶。需要 feedback:list 权限")
    @GetMapping("/list")
    @RequirePermission("feedback:list")
    public Result<PageResult<Feedback>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "类型（appointment 预约咨询 / feedback 留言反馈）") @RequestParam(required = false) String type,
            @Parameter(description = "状态（pending 待处理 / handled 已处理）") @RequestParam(required = false) String status,
            @Parameter(description = "关键字：姓名/手机号/内容/意向车型模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "提交时间起，格式 yyyy-MM-dd") @RequestParam(required = false) String startDate,
            @Parameter(description = "提交时间止，格式 yyyy-MM-dd") @RequestParam(required = false) String endDate) {
        IPage<Feedback> page = feedbackService.getFeedbackList(
                pageNum, pageSize, type, status, keyword, startDate, endDate);
        return Result.ok(PageResult.of(page));
    }

    /** 统计概览：total/pending/handled/today/upcoming/appointment/feedbackCount */
    @Operation(summary = "反馈统计概览", description = "返回 total/pending/handled/today/upcoming/appointment/feedbackCount 等统计指标。需要 feedback:list 权限")
    @GetMapping("/stats")
    @RequirePermission("feedback:list")
    public Result<java.util.Map<String, Object>> stats() {
        return Result.ok(feedbackService.getFeedbackStats());
    }

    /** 记录详情（含关联会员信息） */
    @Operation(summary = "反馈记录详情", description = "查单条记录并回填关联会员信息，记录不存在或已删除时报错。需要 feedback:list 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("feedback:list")
    public Result<Feedback> detail(@Parameter(description = "记录ID") @PathVariable Long id) {
        return Result.ok(feedbackService.getFeedbackDetail(id));
    }

    /** 标记已处理（必填处理备注：沟通结果；防重复处理） */
    @Operation(summary = "标记已处理", description = "处理备注（沟通结果）必填；仅待处理记录可操作，条件更新防并发重复处理，处理人与处理时间由后端记录。需要 feedback:process 权限")
    @PostMapping("/process/{id}")
    @RequirePermission("feedback:process")
    public Result<Void> process(@Parameter(description = "记录ID") @PathVariable Long id, @Valid @RequestBody FeedbackProcessDTO dto) {
        feedbackService.processFeedback(id, dto.getRemark());
        return Result.ok();
    }

    /** 修改处理备注（仅已处理记录） */
    @Operation(summary = "修改处理备注", description = "仅已处理（handled）记录可修改备注，备注不能为空。需要 feedback:update 权限")
    @PutMapping("/remark/{id}")
    @RequirePermission("feedback:update")
    public Result<Void> updateRemark(@Parameter(description = "记录ID") @PathVariable Long id, @Valid @RequestBody FeedbackProcessDTO dto) {
        feedbackService.updateRemark(id, dto.getRemark());
        return Result.ok();
    }

    /** 删除记录（物理删除，用于清理垃圾数据） */
    @Operation(summary = "删除反馈记录", description = "物理删除，用于清理垃圾数据；记录不存在或已删除时报错。需要 feedback:delete 权限")
    @DeleteMapping("/delete/{id}")
    @RequirePermission("feedback:delete")
    public Result<Void> delete(@Parameter(description = "记录ID") @PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return Result.ok();
    }
}
