package com.car.rental.module.marketing.controller;

import com.car.rental.common.result.Result;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.MemberCoupon;
import com.car.rental.module.marketing.service.CustomerCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * C端优惠券接口（供 customer-client 调用）
 * 路径前缀 /api/customer/coupon，独立于后台 /api/coupon，避免与 {id} 路径冲突
 * 鉴权由 C 端网关处理，memberId 通过参数传入
 */
@RestController
@RequestMapping("/api/customer/coupon")
@RequiredArgsConstructor
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;

    /** 可领券列表 */
    @GetMapping("/available")
    public Result<List<Coupon>> listAvailable() {
        return Result.ok(customerCouponService.listAvailable());
    }

    /** 券详情 */
    @GetMapping("/{id}")
    public Result<Coupon> detail(@PathVariable Long id) {
        return Result.ok(customerCouponService.getCouponDetail(id));
    }

    /** 我的券（可选状态筛选：unused/locked/used/expired） */
    @GetMapping("/mine")
    public Result<List<MemberCoupon>> mine(
            @RequestParam Long memberId,
            @RequestParam(required = false) String status) {
        return Result.ok(customerCouponService.listMine(memberId, status));
    }

    /** 下单可用券（一辆车下可用多张，但下单只能选一张，由前端限制） */
    @GetMapping("/usable")
    public Result<List<MemberCoupon>> usable(
            @RequestParam Long memberId,
            @RequestParam Long carId,
            @RequestParam BigDecimal amount) {
        return Result.ok(customerCouponService.listUsable(memberId, carId, amount));
    }

    /** 领取优惠券，返回领取后的 member_coupon.id */
    @PostMapping("/receive/{couponId}")
    public Result<Long> receive(
            @PathVariable Long couponId,
            @RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        String source = body.get("source") == null ? "manual" : body.get("source").toString();
        return Result.ok(customerCouponService.receive(memberId, couponId, source));
    }

    /** 锁定优惠券（下单预占） */
    @PostMapping("/lock")
    public Result<Void> lock(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        customerCouponService.lock(memberCouponId, memberId);
        return Result.ok();
    }

    /** 取消锁定 */
    @PostMapping("/cancel-lock")
    public Result<Void> cancelLock(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        customerCouponService.cancelLock(memberCouponId, memberId);
        return Result.ok();
    }

    /** 核销（订单完成时调用，幂等） */
    @PostMapping("/verify")
    public Result<Void> verify(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        Long orderId = body.get("orderId") == null ? null : Long.valueOf(body.get("orderId").toString());
        customerCouponService.verify(memberCouponId, memberId, orderId);
        return Result.ok();
    }

    /** 计算优惠金额（下单预览用，不实际核销） */
    @PostMapping("/calculate")
    public Result<BigDecimal> calculate(@RequestBody Map<String, Object> body) {
        Long couponId = Long.valueOf(body.get("couponId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return Result.ok(customerCouponService.calculateDiscount(couponId, amount));
    }
}
