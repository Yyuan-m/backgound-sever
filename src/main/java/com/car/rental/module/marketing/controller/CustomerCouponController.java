package com.car.rental.module.marketing.controller;

import com.car.rental.common.result.Result;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.MemberCoupon;
import com.car.rental.module.marketing.service.CustomerCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "客户优惠券", description = "C 端优惠券接口：可领列表、我的券、下单可用券、领取/锁定/核销与优惠金额计算；鉴权由 C 端网关处理，memberId 通过参数传入")
@RestController
@RequestMapping("/api/customer/coupon")
@RequiredArgsConstructor
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;

    /** 可领券列表 */
    @Operation(summary = "可领券列表", description = "已投放 + 有效期内 + 有库存的优惠券；鉴权由 C 端网关处理")
    @GetMapping("/available")
    public Result<List<Coupon>> listAvailable() {
        return Result.ok(customerCouponService.listAvailable());
    }

    /** 券详情 */
    @Operation(summary = "券详情", description = "查询单张优惠券详情；鉴权由 C 端网关处理")
    @GetMapping("/{id}")
    public Result<Coupon> detail(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.ok(customerCouponService.getCouponDetail(id));
    }

    /** 我的券（可选状态筛选：unused/locked/used/expired） */
    @Operation(summary = "我的券列表", description = "查询会员持有的券（跨库 JOIN 优惠券模板），可按状态筛选；鉴权由 C 端网关处理")
    @GetMapping("/mine")
    public Result<List<MemberCoupon>> mine(
            @Parameter(description = "会员ID") @RequestParam Long memberId,
            @Parameter(description = "状态筛选（unused 未使用 / locked 已锁定 / used 已核销 / expired 已过期）") @RequestParam(required = false) String status) {
        return Result.ok(customerCouponService.listMine(memberId, status));
    }

    /** 下单可用券（一辆车下可用多张，但下单只能选一张，由前端限制） */
    @Operation(summary = "下单可用券", description = "查询某会员在某车辆某订单金额下可用的未使用券；一辆车下可用多张但下单只能选一张（由前端限制）；鉴权由 C 端网关处理")
    @GetMapping("/usable")
    public Result<List<MemberCoupon>> usable(
            @Parameter(description = "会员ID") @RequestParam Long memberId,
            @Parameter(description = "车辆ID") @RequestParam Long carId,
            @Parameter(description = "订单金额") @RequestParam BigDecimal amount) {
        return Result.ok(customerCouponService.listUsable(memberId, carId, amount));
    }

    /** 领取优惠券，返回领取后的 member_coupon.id */
    @Operation(summary = "领取优惠券", description = "原子扣减库存并写入会员券记录（受每人限领限制），返回领取后的 member_coupon.id；请求体含 memberId、source（来源，默认 manual）；鉴权由 C 端网关处理")
    @PostMapping("/receive/{couponId}")
    public Result<Long> receive(
            @Parameter(description = "优惠券ID") @PathVariable Long couponId,
            @RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        String source = body.get("source") == null ? "manual" : body.get("source").toString();
        return Result.ok(customerCouponService.receive(memberId, couponId, source));
    }

    /** 锁定优惠券（下单预占） */
    @Operation(summary = "锁定优惠券", description = "下单预占（unused → locked），请求体含 memberCouponId、memberId；鉴权由 C 端网关处理")
    @PostMapping("/lock")
    public Result<Void> lock(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        customerCouponService.lock(memberCouponId, memberId);
        return Result.ok();
    }

    /** 取消锁定 */
    @Operation(summary = "取消锁定优惠券", description = "订单创建失败/取消时释放预占（locked → unused），请求体含 memberCouponId、memberId；鉴权由 C 端网关处理")
    @PostMapping("/cancel-lock")
    public Result<Void> cancelLock(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        customerCouponService.cancelLock(memberCouponId, memberId);
        return Result.ok();
    }

    /** 核销（订单完成时调用，幂等） */
    @Operation(summary = "核销优惠券", description = "订单完成时核销（locked → used，回写订单 ID 并累加券核销数），幂等可重复调用；请求体含 memberCouponId、memberId、orderId；鉴权由 C 端网关处理")
    @PostMapping("/verify")
    public Result<Void> verify(@RequestBody Map<String, Object> body) {
        Long memberCouponId = Long.valueOf(body.get("memberCouponId").toString());
        Long memberId = Long.valueOf(body.get("memberId").toString());
        Long orderId = body.get("orderId") == null ? null : Long.valueOf(body.get("orderId").toString());
        customerCouponService.verify(memberCouponId, memberId, orderId);
        return Result.ok();
    }

    /** 计算优惠金额（下单预览用，不实际核销） */
    @Operation(summary = "计算优惠金额", description = "下单预览用，不实际核销；折扣券按折扣率计算并封顶，满减券满足最低消费门槛抵扣面值，时长券不直接抵扣金额返回 0；请求体含 couponId、amount；鉴权由 C 端网关处理")
    @PostMapping("/calculate")
    public Result<BigDecimal> calculate(@RequestBody Map<String, Object> body) {
        Long couponId = Long.valueOf(body.get("couponId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return Result.ok(customerCouponService.calculateDiscount(couponId, amount));
    }
}
