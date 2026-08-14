package com.car.rental.module.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.CouponCar;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.entity.MemberCoupon;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.marketing.mapper.CouponCarMapper;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.mapper.MemberCouponMapper;
import com.car.rental.module.marketing.service.CustomerCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCouponServiceImpl implements CustomerCouponService {

    private final CouponMapper couponMapper;
    private final MemberCouponMapper memberCouponMapper;
    private final CouponCarMapper couponCarMapper;
    private final CustomerOrderMapper customerOrderMapper;

    @Override
    public List<Coupon> listAvailable() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, "published")
               .eq(Coupon::getPublished, 1)
               .le(Coupon::getValidStartTime, now)
               .gt(Coupon::getValidEndTime, now)
               .orderByDesc(Coupon::getCreatedAt);
        List<Coupon> list = couponMapper.selectList(wrapper);
        // 过滤库存不足的
        return list.stream()
                .filter(c -> c.getTotalCount() == -1 || c.getReceivedCount() == null || c.getReceivedCount() < c.getTotalCount())
                .collect(Collectors.toList());
    }

    @Override
    public Coupon getCouponDetail(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        coupon.setCarIds(couponMapper.selectCarIdsByCouponId(id));
        return coupon;
    }

    @Override
    public List<MemberCoupon> listMine(Long memberId, String status) {
        List<MemberCoupon> all = memberCouponMapper.selectMyCoupons(memberId);
        if (status != null && !status.isEmpty()) {
            // 自动标记过期
            LocalDateTime now = LocalDateTime.now();
            List<MemberCoupon> result = new ArrayList<>();
            for (MemberCoupon mc : all) {
                if ("unused".equals(status) && mc.getExpireTime() != null && mc.getExpireTime().isBefore(now)
                        && "unused".equals(mc.getStatus())) {
                    mc.setStatus("expired");
                }
                if (status.equals(mc.getStatus())) {
                    result.add(mc);
                }
            }
            return result;
        }
        return all;
    }

    @Override
    public List<MemberCoupon> listUsable(Long memberId, Long carId, BigDecimal amount) {
        List<MemberCoupon> mine = memberCouponMapper.selectMyCoupons(memberId);
        LocalDateTime now = LocalDateTime.now();
        List<MemberCoupon> usable = new ArrayList<>();
        for (MemberCoupon mc : mine) {
            if (!"unused".equals(mc.getStatus())) continue;
            if (mc.getExpireTime() != null && mc.getExpireTime().isBefore(now)) continue;
            if (mc.getMinAmount() != null && amount != null && amount.compareTo(mc.getMinAmount()) < 0) continue;
            // 指定车辆券需校验 carId
            if ("specified".equals(mc.getApplyScope())) {
                List<Long> carIds = couponMapper.selectCarIdsByCouponId(mc.getCouponId());
                if (carId == null || !carIds.contains(carId)) continue;
            }
            usable.add(mc);
        }
        return usable;
    }

    @Override
    @Transactional
    public Long receive(Long memberId, Long couponId, String source) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!"published".equals(coupon.getStatus()) || coupon.getPublished() == null || coupon.getPublished() != 1) {
            throw new BusinessException("优惠券未投放，不可领取");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidStartTime() != null && coupon.getValidStartTime().isAfter(now)) {
            throw new BusinessException("优惠券尚未生效");
        }
        if (coupon.getValidEndTime() != null && coupon.getValidEndTime().isBefore(now)) {
            throw new BusinessException("优惠券已过期");
        }
        // 库存校验
        if (coupon.getTotalCount() != -1 && coupon.getReceivedCount() != null
                && coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已领完");
        }
        // 每人限领
        int perUserLimit = coupon.getPerUserLimit() == null ? 1 : coupon.getPerUserLimit();
        int received = memberCouponMapper.countReceivedByUser(memberId, couponId);
        if (received >= perUserLimit) {
            throw new BusinessException("已超过每人限领数量（" + perUserLimit + " 张）");
        }
        // 原子扣减库存
        int affected = couponMapper.incrReceivedCount(couponId);
        if (affected == 0) {
            throw new BusinessException("优惠券已被抢空");
        }
        // 写 member_coupon
        try {
            MemberCoupon mc = new MemberCoupon();
            mc.setMemberId(memberId);
            mc.setCouponId(couponId);
            mc.setStatus("unused");
            mc.setCode(generateCode());
            mc.setClaimTime(now);
            mc.setExpireTime(coupon.getValidEndTime());
            mc.setSource(source == null ? "manual" : source);
            mc.setVersion(0);
            memberCouponMapper.insert(mc);
            return mc.getId();
        } catch (Exception e) {
            // 回滚库存
            couponMapper.decrReceivedCount(couponId);
            log.error("领取优惠券失败，已回滚库存 couponId={}, memberId={}", couponId, memberId, e);
            throw new BusinessException("领取失败，请重试");
        }
    }

    @Override
    @Transactional
    public void lock(Long memberCouponId, Long memberId) {
        MemberCoupon mc = getAndCheckOwnership(memberCouponId, memberId);
        if (!"unused".equals(mc.getStatus())) {
            throw new BusinessException("仅未使用的券可锁定，当前状态: " + mc.getStatus());
        }
        if (mc.getExpireTime() != null && mc.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }
        int affected = memberCouponMapper.updateStatusWithVersion(memberCouponId, "unused", "locked", mc.getVersion());
        if (affected == 0) {
            throw new BusinessException("锁定失败，券状态已变更或已被使用");
        }
    }

    @Override
    @Transactional
    public void cancelLock(Long memberCouponId, Long memberId) {
        MemberCoupon mc = getAndCheckOwnership(memberCouponId, memberId);
        if (!"locked".equals(mc.getStatus())) {
            throw new BusinessException("仅锁定中的券可取消锁定，当前状态: " + mc.getStatus());
        }
        int affected = memberCouponMapper.updateStatusWithVersion(memberCouponId, "locked", "unused", mc.getVersion());
        if (affected == 0) {
            throw new BusinessException("取消锁定失败，券状态已变更");
        }
    }

    @Override
    @Transactional
    public void verify(Long memberCouponId, Long memberId, Long orderId) {
        MemberCoupon mc = getAndCheckOwnership(memberCouponId, memberId);
        if ("used".equals(mc.getStatus())) {
            // 幂等：已核销直接返回
            return;
        }
        if (!"locked".equals(mc.getStatus())) {
            throw new BusinessException("仅锁定中的券可核销，当前状态: " + mc.getStatus());
        }
        // 核销 member_coupon（带乐观锁）
        int affected = memberCouponMapper.verifyWithOrder(memberCouponId, orderId, mc.getVersion());
        if (affected == 0) {
            throw new BusinessException("核销失败，券状态已变更");
        }
        // coupon.used_count++（原子）
        couponMapper.incrUsedCount(mc.getCouponId());

        // 回写 customer_order.coupon_id（修复历史设计缺陷：原 addOrder 不保证回写 coupon_id）
        // 同一事务内，跨库 UPDATE 由 Spring 统一管理（同 MySQL 实例下两库）
        if (orderId != null && mc.getCouponId() != null) {
            customerOrderMapper.update(null, new LambdaUpdateWrapper<CustomerOrder>()
                    .eq(CustomerOrder::getId, orderId)
                    .set(CustomerOrder::getCouponId, mc.getCouponId()));
        }
    }

    @Override
    public BigDecimal calculateDiscount(Long couponId, BigDecimal amount) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        return doCalculate(coupon, amount);
    }

    /**
     * 优惠金额计算（核心算法，供订单调用）
     * discount 折扣券：discount = amount * (1 - value)，封顶 discount_cap
     * deduction 满减券：discount = value（满足 min_amount 门槛）
     * duration 时长券：不直接抵扣金额，返回 0（由订单层处理加天数）
     */
    public static BigDecimal doCalculate(Coupon coupon, BigDecimal amount) {
        if (coupon == null || amount == null) return BigDecimal.ZERO;
        if (coupon.getMinAmount() != null && amount.compareTo(coupon.getMinAmount()) < 0) return BigDecimal.ZERO;
        BigDecimal discount;
        switch (coupon.getType()) {
            case "discount":
                // 折扣值 0.88 表示88折，优惠 = 原价 * (1 - 0.88)
                discount = amount.multiply(BigDecimal.ONE.subtract(coupon.getValue()))
                        .setScale(2, RoundingMode.HALF_UP);
                if (coupon.getDiscountCap() != null && discount.compareTo(coupon.getDiscountCap()) > 0) {
                    discount = coupon.getDiscountCap();
                }
                break;
            case "deduction":
                discount = coupon.getValue();
                break;
            case "duration":
                // 时长券不抵扣金额
                discount = BigDecimal.ZERO;
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        // 优惠不能超过原价
        if (discount.compareTo(amount) > 0) discount = amount;
        return discount;
    }

    private MemberCoupon getAndCheckOwnership(Long memberCouponId, Long memberId) {
        MemberCoupon mc = memberCouponMapper.selectById(memberCouponId);
        if (mc == null) {
            throw new BusinessException("用户券不存在");
        }
        if (!mc.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作他人优惠券");
        }
        return mc;
    }

    private String generateCode() {
        return "MC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
