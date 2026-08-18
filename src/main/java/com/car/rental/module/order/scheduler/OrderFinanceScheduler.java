package com.car.rental.module.order.scheduler;

import com.car.rental.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单财务定时任务
 *
 * 背景：C 端 customer-server 的 autoCompleteOrders() 直接 updateById 将到期订单
 * 置为 completed，绕过了管理后台的 updateOrderStatus()，导致财务流水（rental 收入 /
 * rental_cost 支出）和发票未生成。本调度器在管理后台兜底：
 *   1. autoCompleteExpiredOrders：renting 且 end_date < 今天 → completed（走 updateOrderStatus
 *      保证发票+流水生成；与 C 端逻辑对称，避免管理后台完全依赖 C 端触发）
 *   2. backfillMissingFinanceRecords：兜底补生成已 completed 但缺失流水的订单数据
 *      （用于修复 C 端已自动完成但未生成流水的遗留数据）
 *
 * 触发频率：每 30 分钟一次。两条任务均幂等，可安全重复执行。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFinanceScheduler {

    private final OrderService orderService;

    /**
     * 每 30 分钟自动完成到期订单 + 回补缺失财务流水/发票。
     * fixedDelay：上一次执行结束后再等 30 分钟，避免任务堆积。
     * initialDelay：启动后 1 分钟首次执行，便于服务起来后尽快修复历史数据。
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000L, initialDelay = 60 * 1000L)
    public void runAutoCompleteAndBackfill() {
        try {
            int completed = orderService.autoCompleteExpiredOrders();
            int backfilled = orderService.backfillMissingFinanceRecords();
            if (completed > 0 || backfilled > 0) {
                log.info("定时任务完成：自动完成订单 {} 单，回补财务流水 {} 单", completed, backfilled);
            }
        } catch (Exception e) {
            log.error("订单财务定时任务执行失败: {}", e.getMessage(), e);
        }
    }
}
