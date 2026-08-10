package com.car.rental.module.statistics.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.module.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @RequirePermission("dashboard:dashboard")
    public Result<?> getDashboard() {
        return Result.ok(statisticsService.getDashboard());
    }

    @GetMapping("/order-trend")
    @RequirePermission("dashboard:order-trend")
    public Result<?> getOrderTrend() {
        return Result.ok(statisticsService.getOrderTrend());
    }

    @GetMapping("/revenue-data")
    @RequirePermission("dashboard:revenue-data")
    public Result<?> getRevenueData() {
        return Result.ok(statisticsService.getRevenueData());
    }

    @GetMapping("/vehicle-type")
    @RequirePermission("dashboard:vehicle-type")
    public Result<?> getVehicleType() {
        return Result.ok(statisticsService.getVehicleType());
    }

    @GetMapping("/latest-orders")
    @RequirePermission("dashboard:latest-orders")
    public Result<?> getLatestOrders() {
        return Result.ok(statisticsService.getLatestOrders());
    }

    @GetMapping("/latest-customers")
    @RequirePermission("dashboard:latest-customers")
    public Result<?> getLatestCustomers() {
        return Result.ok(statisticsService.getLatestCustomers());
    }

    @GetMapping("/vehicle-hot")
    @RequirePermission("dashboard:vehicle-hot")
    public Result<?> getVehicleHot() {
        return Result.ok(statisticsService.getVehicleHot());
    }

    @GetMapping("/repurchase-data")
    @RequirePermission("dashboard:repurchase-data")
    public Result<?> getRepurchaseData() {
        return Result.ok(statisticsService.getRepurchaseData());
    }

    @GetMapping("/peak-hours")
    @RequirePermission("dashboard:peak-hours")
    public Result<?> getPeakHours() {
        return Result.ok(statisticsService.getPeakHours());
    }
}