package com.car.rental.module.statistics.service;

import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getDashboard();

    List<Map<String, Object>> getOrderTrend();

    List<Map<String, Object>> getRevenueData();

    List<Map<String, Object>> getVehicleType();

    List<CustomerOrder> getLatestOrders();

    List<CustomerInfo> getLatestCustomers();

    List<Map<String, Object>> getVehicleHot();

    List<Map<String, Object>> getRepurchaseData();

    List<Map<String, Object>> getPeakHours();
}