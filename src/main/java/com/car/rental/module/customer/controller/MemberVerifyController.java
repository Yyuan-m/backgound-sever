package com.car.rental.module.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.MemberVerifyRecord;
import com.car.rental.module.customer.dto.VerifyReviewDTO;
import com.car.rental.module.customer.service.MemberVerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员实名认证审核（数据来自 car_rental_customer 库，人工审核）
 */
@Tag(name = "会员认证审核", description = "会员实名认证记录的人工审核：列表、统计、详情、通过/驳回")
@RestController
@RequestMapping("/api/customer/verify")
@RequiredArgsConstructor
public class MemberVerifyController {

    private final MemberVerifyService memberVerifyService;

    /** 认证记录列表（status: pending/approved/rejected） */
    @Operation(summary = "认证记录列表（分页）", description = "按审核状态、关键字筛选认证记录，状态取值 pending 待审核 / approved 已通过 / rejected 已驳回。需要 customer:list 权限")
    @GetMapping("/list")
    @RequirePermission("customer:list")
    public Result<PageResult<MemberVerifyRecord>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "审核状态：pending 待审核 / approved 已通过 / rejected 已驳回") @RequestParam(required = false) String status,
            @Parameter(description = "关键字：姓名/手机号模糊匹配") @RequestParam(required = false) String keyword) {
        IPage<MemberVerifyRecord> page = memberVerifyService.getVerifyList(pageNum, pageSize, status, keyword);
        return Result.ok(PageResult.of(page));
    }

    /** 审核统计概览：total/pending/approved/rejected/today */
    @Operation(summary = "审核统计概览", description = "返回 total 总数 / pending 待审核 / approved 已通过 / rejected 已驳回 / today 今日新增。需要 customer:list 权限")
    @GetMapping("/stats")
    @RequirePermission("customer:list")
    public Result<java.util.Map<String, Object>> stats() {
        return Result.ok(memberVerifyService.getVerifyStats());
    }

    /** 认证记录详情 */
    @Operation(summary = "认证记录详情", description = "查单条认证记录详情，含会员信息与身份证照片等材料。需要 customer:list 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("customer:list")
    public Result<MemberVerifyRecord> detail(@Parameter(description = "认证记录ID") @PathVariable Long id) {
        return Result.ok(memberVerifyService.getVerifyDetail(id));
    }

    /** 审核：通过 / 驳回（驳回需填原因） */
    @Operation(summary = "审核（通过/驳回）", description = "人工审核认证记录：approved=true 通过，approved=false 驳回（驳回需填 rejectReason 原因）。需要 customer:update 权限")
    @PostMapping("/review/{id}")
    @RequirePermission("customer:update")
    public Result<Void> review(@Parameter(description = "认证记录ID") @PathVariable Long id, @RequestBody VerifyReviewDTO dto) {
        if (dto.getApproved() == null) {
            return Result.error("审核结果不能为空");
        }
        memberVerifyService.review(id, dto.getApproved(), dto.getRejectReason());
        return Result.ok();
    }
}
