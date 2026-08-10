package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.CarMaintenance;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CustomerOrderMapper customerOrderMapper;
    private final CarInfoMapper carInfoMapper;
    private final FinanceRecordMapper financeRecordMapper;
    private final CustomerInfoMapper customerInfoMapper;
    private final AfterSalesComplaintMapper afterSalesComplaintMapper;
    private final CarMaintenanceMapper carMaintenanceMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        long totalOrders = customerOrderMapper.selectCount(null);
        dashboard.put("totalOrders", totalOrders);

        long rentingVehicles = carInfoMapper.selectCount(
                new LambdaQueryWrapper<CarInfo>().eq(CarInfo::getStatus, "rented"));
        dashboard.put("rentingVehicles", rentingVehicles);

        long idleVehicles = carInfoMapper.selectCount(
                new LambdaQueryWrapper<CarInfo>().eq(CarInfo::getStatus, "idle"));
        dashboard.put("idleVehicles", idleVehicles);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        List<FinanceRecord> todayRecords = financeRecordMapper.selectList(
                new LambdaQueryWrapper<FinanceRecord>()
                        .eq(FinanceRecord::getType, "rental")
                        .between(FinanceRecord::getCreatedAt, todayStart, todayEnd));
        BigDecimal todayRevenue = todayRecords.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("todayRevenue", todayRevenue);

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        List<FinanceRecord> monthRecords = financeRecordMapper.selectList(
                new LambdaQueryWrapper<FinanceRecord>()
                        .eq(FinanceRecord::getType, "rental")
                        .between(FinanceRecord::getCreatedAt, monthStart, monthEnd));
        BigDecimal monthRevenue = monthRecords.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("monthRevenue", monthRevenue);

        // 年度营收：本年 type=rental 的 amount 求和
        LocalDateTime yearStart = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime yearEnd = yearStart.plusYears(1);
        List<FinanceRecord> yearRecords = financeRecordMapper.selectList(
                new LambdaQueryWrapper<FinanceRecord>()
                        .eq(FinanceRecord::getType, "rental")
                        .between(FinanceRecord::getCreatedAt, yearStart, yearEnd));
        BigDecimal yearRevenue = yearRecords.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("yearRevenue", yearRevenue);

        long todayOrders = customerOrderMapper.selectCount(
                new LambdaQueryWrapper<CustomerOrder>()
                        .between(CustomerOrder::getCreateTime, todayStart, todayEnd));
        dashboard.put("todayOrders", todayOrders);

        long totalCustomers = customerInfoMapper.selectCount(null);
        dashboard.put("totalCustomers", totalCustomers);

        // 逾期订单：status=renting 且 end_date < 今天
        long overdueOrders = customerOrderMapper.selectCount(
                new LambdaQueryWrapper<CustomerOrder>()
                        .eq(CustomerOrder::getStatus, "renting")
                        .lt(CustomerOrder::getEndDate, LocalDate.now()));
        dashboard.put("overdueOrders", overdueOrders);

        // 出租率
        long totalVehicles = carInfoMapper.selectCount(null);
        long renting = rentingVehicles;
        double rentalRate = totalVehicles > 0 ? (double) renting / totalVehicles : 0;
        dashboard.put("rentalRate", rentalRate);

        // 待处理投诉
        long pendingComplaints = afterSalesComplaintMapper.selectCount(
                new LambdaQueryWrapper<AfterSalesComplaint>().eq(AfterSalesComplaint::getStatus, "pending"));
        dashboard.put("pendingComplaints", pendingComplaints);

        // 待维保车辆
        long pendingMaintenance = carMaintenanceMapper.selectCount(
                new LambdaQueryWrapper<CarMaintenance>()
                        .in(CarMaintenance::getStatus, "pending", "processing"));
        dashboard.put("pendingMaintenance", pendingMaintenance);

        return dashboard;
    }

    @Override
    public List<Map<String, Object>> getOrderTrend() {
        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(create_time, '%Y-%m') as month",
                        "COUNT(*) as orders",
                        "SUM(total_amount) as revenue")
                .apply("create_time >= DATE_SUB(NOW(), INTERVAL 12 MONTH)")
                .groupBy("DATE_FORMAT(create_time, '%Y-%m')")
                .orderByAsc("month");
        return customerOrderMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getRevenueData() {
        QueryWrapper<FinanceRecord> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(created_at, '%Y-%m') as month",
                        "SUM(CASE WHEN type = 'rental' THEN amount ELSE 0 END) as rental")
                .apply("created_at >= DATE_SUB(NOW(), INTERVAL 7 MONTH)")
                .groupBy("DATE_FORMAT(created_at, '%Y-%m')")
                .orderByAsc("month");
        return financeRecordMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getVehicleType() {
        QueryWrapper<CarInfo> wrapper = new QueryWrapper<>();
        wrapper.select("type as name", "COUNT(*) as value")
                .groupBy("type");
        return carInfoMapper.selectMaps(wrapper);
    }

    @Override
    public List<CustomerOrder> getLatestOrders() {
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CustomerOrder::getCreateTime)
                .last("LIMIT 5");
        return customerOrderMapper.selectList(wrapper);
    }

    @Override
    public List<CustomerInfo> getLatestCustomers() {
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CustomerInfo::getCreatedAt)
                .last("LIMIT 5");
        return customerInfoMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getVehicleHot() {
        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.select("car_name as name", "COUNT(*) as orders", "SUM(total_amount) as revenue")
                .groupBy("car_name")
                .orderByDesc("orders")
                .last("LIMIT 5");
        return customerOrderMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getRepurchaseData() {
        QueryWrapper<CustomerInfo> wrapper = new QueryWrapper<>();
        String caseExpr = "CASE WHEN total_orders = 1 THEN '首次' " +
                "WHEN total_orders BETWEEN 2 AND 3 THEN '2-3次' " +
                "WHEN total_orders BETWEEN 4 AND 6 THEN '4-6次' " +
                "ELSE '7次以上' END";
        wrapper.select(caseExpr + " as name", "COUNT(*) as value")
                .groupBy(caseExpr).orderByAsc("name");
        return customerInfoMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getPeakHours() {
        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.select(
                "CONCAT(LPAD(FLOOR(HOUR(create_time)/2)*2, 2, '0'), '-', LPAD(FLOOR(HOUR(create_time)/2)*2+2, 2, '0')) as hour",
                "COUNT(*) as orders"
        ).groupBy("hour").orderByAsc("hour");
        return customerOrderMapper.selectMaps(wrapper);
    }
}
