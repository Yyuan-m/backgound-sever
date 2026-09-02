package com.car.rental.module.marketing.scheduler;

import com.car.rental.module.marketing.mapper.CouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 优惠券过期自动下线定时任务
 *
 * 规则：已投放（status=published）但超过失效时间（valid_end_time < 当前时间）的优惠券
 * 直接改为下线状态（status=offline, published=0）。
 * 下线后如需重新上线，必须先编辑修改有效期（publish 已校验：过期券不可投放）。
 *
 * 触发频率：每 30 分钟一次。任务幂等，可安全重复执行。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponExpireScheduler {

    private final CouponMapper couponMapper;

    /**
     * 过期券自动下线。
     * fixedDelay：上一次执行结束后再等 30 分钟，避免任务堆积。
     * initialDelay：启动后 30 秒首次执行，服务起来后尽快清理过期券。
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000L, initialDelay = 30 * 1000L)
    public void offlineExpiredCoupons() {
        try {
            int count = couponMapper.offlineExpired();
            if (count > 0) {
                log.info("优惠券过期自动下线：本次下线 {} 张", count);
            }
        } catch (Exception e) {
            log.error("优惠券过期下线定时任务执行失败: {}", e.getMessage(), e);
        }
    }
}
