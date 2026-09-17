package com.car.rental.module.statistics.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.statistics.service.FinanceService;
import com.car.rental.module.statistics.service.FinanceStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "财务管理", description = "财务流水增删改查、营收汇总及财务统计聚合（总览/利润趋势/对账聚合/成本构成/每日收支/车型收支分析）")
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final FinanceStatsService financeStatsService;

    @Operation(summary = "财务流水列表（分页）", description = "按关键字、类型、收支方向筛选财务流水，按创建时间倒序。需要 finance:records 权限")
    @GetMapping("/records")
    @RequirePermission("finance:records")
    public Result<PageResult<FinanceRecord>> getRecords(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：订单号/客户姓名模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "流水类型（rental 租金收入 / 其他为支出类）") @RequestParam(required = false) String type,
            @Parameter(description = "收支方向：inflow 流入（租金类）/ outflow 流出（非租金类）") @RequestParam(required = false) String direction) {
        return Result.ok(financeService.getRecords(pageNum, pageSize, keyword, type, direction));
    }

    @Operation(summary = "财务流水详情", description = "按 ID 查询单条财务流水，不存在时抛业务异常。需要 finance:records 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("finance:records")
    public Result<FinanceRecord> detail(@Parameter(description = "流水ID") @PathVariable Long id) {
        return Result.ok(financeService.getById(id));
    }

    @Operation(summary = "新增财务流水", description = "手工录入财务流水，创建时间自动填充为当前时间。需要 finance:add 权限")
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

    @Operation(summary = "编辑财务流水", description = "按 ID 更新财务流水（类型/订单号/客户/金额/支付方式/状态等）。需要 finance:update 权限")
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
    public Result<Void> update(@Parameter(description = "流水ID") @PathVariable Long id, @RequestBody FinanceRecord record) {
        financeService.updateRecord(id, record);
        return Result.ok();
    }

    @Operation(summary = "删除财务流水", description = "按 ID 逻辑删除财务流水。需要 finance:delete 权限")
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
    public Result<Void> delete(@Parameter(description = "流水ID") @PathVariable Long id) {
        financeService.deleteRecord(id);
        return Result.ok();
    }

    @Operation(summary = "营收汇总", description = "按时间维度聚合各期总金额与流水条数。需要 finance:revenue-summary 权限")
    @GetMapping("/revenue-summary")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getRevenueSummary(
            @Parameter(description = "统计周期：day 按日 / month 按月 / year 按年，默认 month") @RequestParam(defaultValue = "month") String period) {
        return Result.ok(financeService.getRevenueSummary(period));
    }

    // ==================== 财务统计聚合（新） ====================

    /** 财务总览：总营收 / 总成本 / 净利润 */
    @Operation(summary = "财务总览", description = "总营收 / 总成本 / 净利润，含本月/本年维度汇总。需要 finance:revenue-summary 权限")
    @GetMapping("/overview")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getFinanceOverview() {
        return Result.ok(financeStatsService.getFinanceOverview());
    }

    /** 利润分析：按月聚合营收、成本、净利润趋势 */
    @Operation(summary = "利润趋势（按月）", description = "最近 N 个月按月聚合营收、成本、净利润趋势。需要 finance:revenue-summary 权限")
    @GetMapping("/profit-trend")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getProfitTrend(@Parameter(description = "统计月数，默认 6") @RequestParam(defaultValue = "6") Integer months) {
        return Result.ok(financeStatsService.getProfitTrend(months));
    }

    /** 对账聚合：按月从 finance_record 自动聚合各项金额 */
    @Operation(summary = "对账数据聚合（按月）", description = "按月从财务流水自动聚合租金收入/其他费用/净收入，并标记是否已对账及对账人，统计最近 N 个月。需要 finance:reconciliation 权限")
    @GetMapping("/reconciliation/aggregate")
    @RequirePermission("finance:reconciliation")
    public Result<?> getReconciliationAggregate(@Parameter(description = "统计月数，默认 6") @RequestParam(defaultValue = "6") Integer months) {
        return Result.ok(financeStatsService.getReconciliationAgggregate(months));
    }

    /** 车辆成本参考表：日租/日成本/利润率/累计租赁天数/累计成本 */
    @Operation(summary = "车辆成本参考表", description = "列出所有车辆的日租/日成本/利润率/累计租赁天数/累计成本。需要 finance:revenue-summary 权限")
    @GetMapping("/vehicle-cost-reference")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getVehicleCostReference() {
        return Result.ok(financeStatsService.getVehicleCostReference());
    }

    /** 成本构成饼图：车辆租赁/维保/手工成本占比 */
    @Operation(summary = "成本构成饼图", description = "车辆租赁成本 / 维保成本 / 手工录入成本三类占比。需要 finance:revenue-summary 权限")
    @GetMapping("/cost-composition")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getCostComposition(@Parameter(description = "统计周期：total 累计 / month 本月 / year 本年，默认 total") @RequestParam(defaultValue = "total") String period) {
        return Result.ok(financeStatsService.getCostComposition(period));
    }

    /** 每日收支：指定月份按日聚合收入和成本 */
    @Operation(summary = "每日收支", description = "指定月份按日聚合收入和成本，不传月份默认当月。需要 finance:revenue-summary 权限")
    @GetMapping("/daily-breakdown")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getDailyBreakdown(@Parameter(description = "月份，格式 yyyy-MM，不传默认当月") @RequestParam(required = false) String month) {
        return Result.ok(financeStatsService.getDailyBreakdown(month));
    }

    /** 车型收支分析：按车辆类型分组聚合收入/成本/利润 */
    @Operation(summary = "车型收支分析", description = "按车辆类型分组聚合收入/成本/利润/利润率。需要 finance:revenue-summary 权限")
    @GetMapping("/vehicle-type-breakdown")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getVehicleTypeBreakdown() {
        return Result.ok(financeStatsService.getVehicleTypeBreakdown());
    }

    /** 活动统计：按月聚合优惠券领取数量与核销数量/优惠金额 */
    @Operation(summary = "活动统计（按月）", description = "按月聚合优惠券领取数量（claim_time 分月）与核销数量/优惠金额（use_time 分月，仅已核销），金额取关联订单优惠券折扣（仅已完成订单，与财务统计口径一致）。需要 finance:revenue-summary 权限")
    @GetMapping("/activity-stats")
    @RequirePermission("finance:revenue-summary")
    public Result<?> getActivityStats(@Parameter(description = "统计月数，默认 6") @RequestParam(defaultValue = "6") Integer months) {
        return Result.ok(financeStatsService.getActivityStats(months));
    }
}