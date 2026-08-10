package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.statistics.service.FinanceService;
import com.car.rental.module.statistics.service.FinanceStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final FinanceStatsService financeStatsService;

    @GetMapping("/records")
    @RequirePermission("finance:records")
    public Result<PageResult<FinanceRecord>> getRecords(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String direction) {
        IPage<FinanceRecord> page = financeService.getRecords(pageNum, pageSize, keyword, type, direction);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/detail/{id}")
    @RequirePermission("finance:records")
    public Result<FinanceRecord> detail(@PathVariable Long id) {
        return Result.ok(financeService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("finance:add")
    @LogChanges(
        entityClass = FinanceRecord.class,
        mapperClass = FinanceRecordMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "orderNo:订单号",
            "customerName:客户姓名", "amount:金额", "method:支付方式",
            "status:状态"
        }
    )
    public Result<Void> add(@RequestBody FinanceRecord record) {
        financeService.addRecord(record);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    @RequirePermission("finance:update")
    @LogChanges(
        entityClass = FinanceRecord.class,
        mapperClass = FinanceRecordMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "orderNo:订单号",
            "customerName:客户姓名", "amount:金额", "method:支付方式",
            "status:状态"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody FinanceRecord record) {
        financeService.updateRecord(id, record);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission("finance:delete")
    @LogChanges(
        entityClass = FinanceRecord.class,
        mapperClass = FinanceRecordMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "orderNo:订单号",
            "customerName:客户姓名", "amount:金额", "method:支付方式",
            "status:状态"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        financeService.deleteRecord(id);
        return Result.ok();
    }

    @GetMapping("/revenue-summary")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getRevenueSummary(
            @RequestParam(defaultValue = "month") String period) {
        return Result.ok(financeService.getRevenueSummary(period));
    }

    // ==================== 财务统计聚合（新） ====================

    /** 财务总览：总营收 / 总成本 / 净利润 */
    @GetMapping("/overview")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getFinanceOverview() {
        return Result.ok(financeStatsService.getFinanceOverview());
    }

    /** 利润分析：按月聚合营收、成本、净利润趋势 */
    @GetMapping("/profit-trend")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getProfitTrend(@RequestParam(defaultValue = "6") Integer months) {
        return Result.ok(financeStatsService.getProfitTrend(months));
    }

    /** 对账聚合：按月从 finance_record 自动聚合各项金额 */
    @GetMapping("/reconciliation/aggregate")
    @RequirePermission("finance:reconciliation")
    public Result<?> getReconciliationAggregate(@RequestParam(defaultValue = "6") Integer months) {
        return Result.ok(financeStatsService.getReconciliationAgggregate(months));
    }

    /** 车辆成本参考表：日租/日成本/利润率/累计租赁天数/累计成本 */
    @GetMapping("/vehicle-cost-reference")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getVehicleCostReference() {
        return Result.ok(financeStatsService.getVehicleCostReference());
    }

    /** 成本构成饼图：车辆租赁/维保/手工成本占比 */
    @GetMapping("/cost-composition")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getCostComposition(@RequestParam(defaultValue = "total") String period) {
        return Result.ok(financeStatsService.getCostComposition(period));
    }

    /** 每日收支：指定月份按日聚合收入和成本 */
    @GetMapping("/daily-breakdown")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getDailyBreakdown(@RequestParam(required = false) String month) {
        return Result.ok(financeStatsService.getDailyBreakdown(month));
    }

    /** 车型收支分析：按车辆类型分组聚合收入/成本/利润 */
    @GetMapping("/vehicle-type-breakdown")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getVehicleTypeBreakdown() {
        return Result.ok(financeStatsService.getVehicleTypeBreakdown());
    }
}