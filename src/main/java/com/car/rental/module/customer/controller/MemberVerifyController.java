package com.car.rental.module.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.MemberVerifyRecord;
import com.car.rental.module.customer.dto.VerifyReviewDTO;
import com.car.rental.module.customer.service.MemberVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员实名认证审核（数据来自 car_rental_customer 库，人工审核）
 */
@RestController
@RequestMapping("/api/customer/verify")
@RequiredArgsConstructor
public class MemberVerifyController {

    private final MemberVerifyService memberVerifyService;

    /** 认证记录列表（status: pending/approved/rejected） */
    @GetMapping("/list")
    @RequirePermission("customer:list")
    public Result<PageResult<MemberVerifyRecord>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        IPage<MemberVerifyRecord> page = memberVerifyService.getVerifyList(pageNum, pageSize, status, keyword);
        return Result.ok(PageResult.of(page));
    }

    /** 审核统计概览：total/pending/approved/rejected/today */
    @GetMapping("/stats")
    @RequirePermission("customer:list")
    public Result<java.util.Map<String, Object>> stats() {
        return Result.ok(memberVerifyService.getVerifyStats());
    }

    /** 认证记录详情 */
    @GetMapping("/detail/{id}")
    @RequirePermission("customer:list")
    public Result<MemberVerifyRecord> detail(@PathVariable Long id) {
        return Result.ok(memberVerifyService.getVerifyDetail(id));
    }

    /** 审核：通过 / 驳回（驳回需填原因） */
    @PostMapping("/review/{id}")
    @RequirePermission("customer:update")
    public Result<Void> review(@PathVariable Long id, @RequestBody VerifyReviewDTO dto) {
        if (dto.getApproved() == null) {
            return Result.error("审核结果不能为空");
        }
        memberVerifyService.review(id, dto.getApproved(), dto.getRejectReason());
        return Result.ok();
    }
}
