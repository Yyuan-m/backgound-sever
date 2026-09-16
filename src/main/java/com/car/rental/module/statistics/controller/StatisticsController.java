package com.car.rental.module.statistics.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.module.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据统计/仪表盘", description = "仪表盘核心指标、订单与营收趋势、车型分布、热门车辆、复购率与高峰时段等统计数据")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "仪表盘核心指标", description = "总订单数、租赁中/空闲车辆数、今日/本月/本年营收（租金类流水求和）、今日订单数、客户总数、逾期订单数、出租率、待处理投诉数、待维保车辆数。需要 dashboard:dashboard 权限")
    @GetMapping("/dashboard")
    @RequirePermission("dashboard:dashboard")
    public Result<?> getDashboard() {
        return Result.ok(statisticsService.getDashboard());
    }

    @Operation(summary = "订单趋势（近12个月）", description = "按月聚合近 12 个月的订单数与营收（总金额求和）。需要 dashboard:order-trend 权限")
    @GetMapping("/order-trend")
    @RequirePermission("dashboard:order-trend")
    public Result<?> getOrderTrend() {
        return Result.ok(statisticsService.getOrderTrend());
    }

    @Operation(summary = "营收数据（近7个月）", description = "按月聚合近 7 个月的租金收入（type=rental 财务流水求和）。需要 dashboard:revenue-data 权限")
    @GetMapping("/revenue-data")
    @RequirePermission("dashboard:revenue-data")
    public Result<?> getRevenueData() {
        return Result.ok(statisticsService.getRevenueData());
    }

    @Operation(summary = "车型分布", description = "按车辆类型分组统计数量，用于饼图展示。需要 dashboard:vehicle-type 权限")
    @GetMapping("/vehicle-type")
    @RequirePermission("dashboard:vehicle-type")
    public Result<?> getVehicleType() {
        return Result.ok(statisticsService.getVehicleType());
    }

    @Operation(summary = "最新订单（5条）", description = "按创建时间倒序取最新 5 条订单。需要 dashboard:latest-orders 权限")
    @GetMapping("/latest-orders")
    @RequirePermission("dashboard:latest-orders")
    public Result<?> getLatestOrders() {
        return Result.ok(statisticsService.getLatestOrders());
    }

    @Operation(summary = "最新租客（5条）", description = "取最新 5 位租客，含实时聚合的累计消费/订单数，字段与租客列表接口保持一致。需要 dashboard:latest-customers 权限")
    @GetMapping("/latest-customers")
    @RequirePermission("dashboard:latest-customers")
    public Result<?> getLatestCustomers() {
        return Result.ok(statisticsService.getLatestCustomers());
    }

    @Operation(summary = "热门车辆TOP5", description = "按订单数倒序统计前 5 名车辆的订单数与营收。需要 dashboard:vehicle-hot 权限")
    @GetMapping("/vehicle-hot")
    @RequirePermission("dashboard:vehicle-hot")
    public Result<?> getVehicleHot() {
        return Result.ok(statisticsService.getVehicleHot());
    }

    @Operation(summary = "复购率分布", description = "按客户累计订单数分组统计人数：首次 / 2-3次 / 4-6次 / 7次以上。需要 dashboard:repurchase-data 权限")
    @GetMapping("/repurchase-data")
    @RequirePermission("dashboard:repurchase-data")
    public Result<?> getRepurchaseData() {
        return Result.ok(statisticsService.getRepurchaseData());
    }

    @Operation(summary = "高峰时段分布", description = "按 2 小时时段聚合下单量，用于柱状图展示。需要 dashboard:peak-hours 权限")
    @GetMapping("/peak-hours")
    @RequirePermission("dashboard:peak-hours")
    public Result<?> getPeakHours() {
        return Result.ok(statisticsService.getPeakHours());
    }
}