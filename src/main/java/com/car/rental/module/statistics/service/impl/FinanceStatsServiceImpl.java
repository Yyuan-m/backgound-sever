package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.car.rental.entity.CostRecord;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.entity.Reconciliation;
import com.car.rental.entity.CarMaintenance;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import com.car.rental.module.statistics.service.FinanceStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务统计聚合实现
 *
 * 设计原则（统一算法，各模块数据一致）：
 * 1. 营收 = finance_record 中 type=rental 的 amount 求和（租金收入）
 * 2. 成本 = cost_record.amount 求和（手工录入的运营/保险成本）
 *          + car_maintenance.cost 求和（维保成本，从车辆维保业务自动派生）
 *          + 车辆租赁成本 = sum(car_info.daily_cost × customer_order.days)（从订单+车辆自动派生）
 * 3. 净利润 = 营收 - 成本
 * 4. 对账数据按月从 finance_record 聚合，避免与流水脱节
 * 5. 车辆成本参考表：列出每辆车的日租/日成本/利润率/累计租赁天数/累计成本
 */
@Service
@RequiredArgsConstructor
public class FinanceStatsServiceImpl implements FinanceStatsService {

    private final FinanceRecordMapper financeRecordMapper;
    private final CostRecordMapper costRecordMapper;
    private final CarMaintenanceMapper carMaintenanceMapper;
    private final ReconciliationMapper reconciliationMapper;
    private final CarInfoMapper carInfoMapper;
    private final CustomerOrderMapper customerOrderMapper;

    @Override
    public Map<String, Object> getFinanceOverview() {
        Map<String, Object> overview = new HashMap<>();

        // ===== 营收：finance_record type=rental 的 amount 求和 =====
        // 总营收
        BigDecimal totalRevenue = sumFinanceAmount(null, null, "rental");
        overview.put("totalRevenue", totalRevenue);

        // 本月营收
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        BigDecimal monthRevenue = sumFinanceAmount(monthStart, monthEnd, "rental");
        overview.put("monthRevenue", monthRevenue);

        // 本年营收
        LocalDateTime yearStart = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime yearEnd = yearStart.plusYears(1);
        BigDecimal yearRevenue = sumFinanceAmount(yearStart, yearEnd, "rental");
        overview.put("yearRevenue", yearRevenue);

        // ===== 成本：cost_record + car_maintenance + 车辆租赁成本 =====
        BigDecimal totalCost = sumCostRecord(null, null)
                .add(sumMaintenanceCost(null, null))
                .add(sumVehicleRentalCost(null, null));
        overview.put("totalCost", totalCost);

        BigDecimal monthCost = sumCostRecord(monthStart, monthEnd)
                .add(sumMaintenanceCost(monthStart, monthEnd))
                .add(sumVehicleRentalCost(monthStart, monthEnd));
        overview.put("monthCost", monthCost);

        BigDecimal yearCost = sumCostRecord(yearStart, yearEnd)
                .add(sumMaintenanceCost(yearStart, yearEnd))
                .add(sumVehicleRentalCost(yearStart, yearEnd));
        overview.put("yearCost", yearCost);

        // ===== 净利润 = 营收 - 成本 =====
        overview.put("totalProfit", totalRevenue.subtract(totalCost));
        overview.put("monthProfit", monthRevenue.subtract(monthCost));
        overview.put("yearProfit", yearRevenue.subtract(yearCost));

        return overview;
    }

    @Override
    public List<Map<String, Object>> getProfitTrend(int months) {
        if (months <= 0) months = 6;

        // 营收按月聚合（type=rental）
        QueryWrapper<FinanceRecord> revenueWrapper = new QueryWrapper<>();
        revenueWrapper.select("DATE_FORMAT(created_at, '%Y-%m') as month",
                        "SUM(amount) as revenue")
                .eq("type", "rental")
                .apply("created_at >= DATE_SUB(NOW(), INTERVAL " + months + " MONTH)")
                .groupBy("DATE_FORMAT(created_at, '%Y-%m')")
                .orderByAsc("month");
        List<Map<String, Object>> revenueList = financeRecordMapper.selectMaps(revenueWrapper);
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (Map<String, Object> r : revenueList) {
            String m = String.valueOf(r.get("month"));
            BigDecimal v = toBigDecimal(r.get("revenue"));
            revenueMap.put(m, v);
        }

        // 成本按月聚合（cost_record + car_maintenance）
        Map<String, BigDecimal> costMap = new LinkedHashMap<>();
        // cost_record
        QueryWrapper<CostRecord> costWrapper = new QueryWrapper<>();
        costWrapper.select("DATE_FORMAT(date, '%Y-%m') as month",
                        "SUM(amount) as cost")
                .apply("date >= DATE_SUB(NOW(), INTERVAL " + months + " MONTH)")
                .groupBy("DATE_FORMAT(date, '%Y-%m')")
                .orderByAsc("month");
        List<Map<String, Object>> costList = costRecordMapper.selectMaps(costWrapper);
        for (Map<String, Object> c : costList) {
            String m = String.valueOf(c.get("month"));
            BigDecimal v = toBigDecimal(c.get("cost"));
            costMap.merge(m, v, BigDecimal::add);
        }
        // car_maintenance（维保成本自动派生）
        QueryWrapper<CarMaintenance> mtWrapper = new QueryWrapper<>();
        mtWrapper.select("DATE_FORMAT(maintenance_date, '%Y-%m') as month",
                        "SUM(cost) as cost")
                .apply("maintenance_date >= DATE_SUB(NOW(), INTERVAL " + months + " MONTH)")
                .groupBy("DATE_FORMAT(maintenance_date, '%Y-%m')")
                .orderByAsc("month");
        List<Map<String, Object>> mtList = carMaintenanceMapper.selectMaps(mtWrapper);
        for (Map<String, Object> m : mtList) {
            String month = String.valueOf(m.get("month"));
            BigDecimal v = toBigDecimal(m.get("cost"));
            costMap.merge(month, v, BigDecimal::add);
        }
        // 车辆租赁成本（daily_cost × 订单天数，按月聚合）
        Map<String, BigDecimal> vehicleCostMap = sumVehicleRentalCostByMonth(months);
        for (Map.Entry<String, BigDecimal> e : vehicleCostMap.entrySet()) {
            costMap.merge(e.getKey(), e.getValue(), BigDecimal::add);
        }

        // 合并所有月份
        Map<String, BigDecimal> allMonths = new LinkedHashMap<>();
        // 生成最近 N 个月的连续月份，避免缺月
        LocalDate now = LocalDate.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            String key = String.format("%04d-%02d", m.getYear(), m.getMonthValue());
            allMonths.put(key, BigDecimal.ZERO);
        }
        allMonths.keySet().forEach(k -> allMonths.put(k, null)); // 占位

        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : allMonths.keySet()) {
            BigDecimal revenue = revenueMap.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal cost = costMap.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal profit = revenue.subtract(cost);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", month);
            row.put("revenue", revenue);
            row.put("cost", cost);
            row.put("profit", profit);
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getReconciliationAgggregate(int months) {
        if (months <= 0) months = 6;

        // 按月从 finance_record 聚合各类金额
        QueryWrapper<FinanceRecord> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(created_at, '%Y-%m') as month",
                        "SUM(CASE WHEN type = 'rental' THEN amount ELSE 0 END) as rental_income",
                        "SUM(CASE WHEN type IN ('overdue_fee','violation_fee','service_fee') THEN amount ELSE 0 END) as fees")
                .apply("created_at >= DATE_SUB(NOW(), INTERVAL " + months + " MONTH)")
                .groupBy("DATE_FORMAT(created_at, '%Y-%m')")
                .orderByAsc("month");
        List<Map<String, Object>> rawList = financeRecordMapper.selectMaps(wrapper);

        // 查询已对账记录，标记状态
        LambdaQueryWrapper<Reconciliation> recWrapper = new LambdaQueryWrapper<>();
        recWrapper.orderByDesc(Reconciliation::getDate);
        List<Reconciliation> recList = reconciliationMapper.selectList(recWrapper);
        Map<String, Reconciliation> recMap = new HashMap<>();
        for (Reconciliation r : recList) {
            if (r.getDate() != null) {
                String key = String.format("%04d-%02d", r.getDate().getYear(), r.getDate().getMonthValue());
                recMap.putIfAbsent(key, r);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawList) {
            String month = String.valueOf(row.get("month"));
            BigDecimal rental = toBigDecimal(row.get("rental_income"));
            BigDecimal fees = toBigDecimal(row.get("fees"));
            // 净收入 = 租金 + 其他费用（费用含违章扣款等，可能为正为负，原值参与）
            BigDecimal netIncome = rental.add(fees);

            Reconciliation rec = recMap.get(month);
            String status = rec != null ? rec.getStatus() : "pending";
            String checkedBy = rec != null && rec.getCheckedBy() != null ? rec.getCheckedBy() : "";
            LocalDateTime checkedAt = rec != null ? rec.getCheckedAt() : null;

            Map<String, Object> r = new LinkedHashMap<>();
            r.put("month", month);
            r.put("rentalIncome", rental);
            r.put("fees", fees);
            r.put("netIncome", netIncome);
            r.put("status", status);
            r.put("checkedBy", checkedBy);
            r.put("checkedAt", checkedAt);
            r.put("reconciliationId", rec != null ? rec.getId() : null);
            result.add(r);
        }
        return result;
    }

    // ==================== 私有工具方法 ====================

    /** 求和 finance_record 指定时间段、指定类型的 amount */
    private BigDecimal sumFinanceAmount(LocalDateTime start, LocalDateTime end, String type) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        if (start != null && end != null) {
            wrapper.between(FinanceRecord::getCreatedAt, start, end);
        }
        List<FinanceRecord> records = financeRecordMapper.selectList(wrapper);
        return records.stream()
                .map(r -> r.getAmount() == null ? BigDecimal.ZERO : r.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 求和 cost_record 指定时间段的 amount */
    private BigDecimal sumCostRecord(LocalDateTime start, LocalDateTime end) {
        QueryWrapper<CostRecord> wrapper = new QueryWrapper<>();
        if (start != null && end != null) {
            wrapper.between("date", start.toLocalDate(), end.toLocalDate());
        }
        List<Map<String, Object>> list = costRecordMapper.selectMaps(
                wrapper.select("IFNULL(SUM(amount),0) as total"));
        if (list.isEmpty()) return BigDecimal.ZERO;
        return toBigDecimal(list.get(0).get("total"));
    }

    /** 求和 car_maintenance 指定时间段的 cost */
    private BigDecimal sumMaintenanceCost(LocalDateTime start, LocalDateTime end) {
        QueryWrapper<CarMaintenance> wrapper = new QueryWrapper<>();
        if (start != null && end != null) {
            wrapper.between("maintenance_date", start.toLocalDate(), end.toLocalDate());
        }
        List<Map<String, Object>> list = carMaintenanceMapper.selectMaps(
                wrapper.select("IFNULL(SUM(cost),0) as total"));
        if (list.isEmpty()) return BigDecimal.ZERO;
        return toBigDecimal(list.get(0).get("total"));
    }

    /**
     * 车辆租赁成本 = sum(car_info.daily_cost × customer_order.days)
     * 通过 join customer_order 和 car_info 计算
     * @param start 开始时间（按 create_time 过滤），null 表示不限
     * @param end 结束时间，null 表示不限
     */
    private BigDecimal sumVehicleRentalCost(LocalDateTime start, LocalDateTime end) {
        // 查询所有车辆，构建 carId -> dailyCost 映射
        List<CarInfo> cars = carInfoMapper.selectList(null);
        Map<Long, BigDecimal> carCostMap = cars.stream()
                .collect(Collectors.toMap(CarInfo::getId,
                        c -> c.getDailyCost() == null ? BigDecimal.ZERO : c.getDailyCost(),
                        (a, b) -> a));
        // 查询订单
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        if (start != null && end != null) {
            wrapper.between(CustomerOrder::getCreateTime, start, end);
        }
        wrapper.isNotNull(CustomerOrder::getCarId);
        List<CustomerOrder> orders = customerOrderMapper.selectList(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (CustomerOrder order : orders) {
            BigDecimal dailyCost = carCostMap.getOrDefault(order.getCarId(), BigDecimal.ZERO);
            int days = order.getDays() == null ? 0 : order.getDays();
            total = total.add(dailyCost.multiply(BigDecimal.valueOf(days)));
        }
        return total;
    }

    /**
     * 车辆租赁成本按月聚合（用于利润趋势图）
     * @return Map<月份, 成本>
     */
    private Map<String, BigDecimal> sumVehicleRentalCostByMonth(int months) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        // 查询所有车辆
        List<CarInfo> cars = carInfoMapper.selectList(null);
        Map<Long, BigDecimal> carCostMap = cars.stream()
                .collect(Collectors.toMap(CarInfo::getId,
                        c -> c.getDailyCost() == null ? BigDecimal.ZERO : c.getDailyCost(),
                        (a, b) -> a));
        // 查询近 N 月订单
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL " + months + " MONTH)")
                .isNotNull(CustomerOrder::getCarId);
        List<CustomerOrder> orders = customerOrderMapper.selectList(wrapper);
        for (CustomerOrder order : orders) {
            if (order.getCreateTime() == null) continue;
            String month = String.format("%04d-%02d", order.getCreateTime().getYear(), order.getCreateTime().getMonthValue());
            BigDecimal dailyCost = carCostMap.getOrDefault(order.getCarId(), BigDecimal.ZERO);
            int days = order.getDays() == null ? 0 : order.getDays();
            result.merge(month, dailyCost.multiply(BigDecimal.valueOf(days)), BigDecimal::add);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getVehicleCostReference() {
        // 查询所有车辆
        List<CarInfo> cars = carInfoMapper.selectList(
                new LambdaQueryWrapper<CarInfo>().orderByDesc(CarInfo::getCreatedAt));
        // 查询所有订单，按 carId 聚合租赁天数
        List<CustomerOrder> orders = customerOrderMapper.selectList(null);
        Map<Long, int[]> carRentalDays = new HashMap<>(); // carId -> [totalDays, orderCount]
        for (CustomerOrder order : orders) {
            if (order.getCarId() == null) continue;
            int days = order.getDays() == null ? 0 : order.getDays();
            carRentalDays.computeIfAbsent(order.getCarId(), k -> new int[2]);
            carRentalDays.get(order.getCarId())[0] += days;
            carRentalDays.get(order.getCarId())[1] += 1;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (CarInfo car : cars) {
            BigDecimal price = car.getDailyPrice() == null ? BigDecimal.ZERO : car.getDailyPrice();
            BigDecimal cost = car.getDailyCost() == null ? BigDecimal.ZERO : car.getDailyCost();
            // 利润率 = (日租 - 日成本) / 日租
            BigDecimal profitMargin = BigDecimal.ZERO;
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = price.subtract(cost).divide(price, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
            }
            int[] rentalInfo = carRentalDays.getOrDefault(car.getId(), new int[2]);
            int totalDays = rentalInfo[0];
            int orderCount = rentalInfo[1];
            BigDecimal totalRentalCost = cost.multiply(BigDecimal.valueOf(totalDays));

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", car.getId());
            row.put("name", car.getName());
            row.put("brand", car.getBrand());
            row.put("plateNumber", car.getPlateNumber());
            row.put("type", car.getType());
            row.put("dailyPrice", price);
            row.put("dailyCost", cost);
            row.put("dailyProfit", price.subtract(cost));
            row.put("profitMargin", profitMargin);
            row.put("totalRentalDays", totalDays);
            row.put("orderCount", orderCount);
            row.put("totalRentalCost", totalRentalCost);
            row.put("status", car.getStatus());
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCostComposition(String period) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if ("month".equals(period)) {
            start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            end = start.plusMonths(1);
        } else if ("year".equals(period)) {
            start = LocalDate.now().withDayOfYear(1).atStartOfDay();
            end = start.plusYears(1);
        }
        // total 时 start/end 为 null，查全部

        BigDecimal vehicleRentalCost = sumVehicleRentalCost(start, end);
        BigDecimal maintenanceCost = sumMaintenanceCost(start, end);
        BigDecimal manualCost = sumCostRecord(start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(buildCostItem("车辆租赁成本", vehicleRentalCost, "#1a365d"));
        result.add(buildCostItem("维保成本", maintenanceCost, "#ef4444"));
        result.add(buildCostItem("手工成本", manualCost, "#c9a96e"));
        return result;
    }

    private Map<String, Object> buildCostItem(String name, BigDecimal value, String color) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("value", value == null ? BigDecimal.ZERO : value);
        item.put("color", color);
        return item;
    }

    @Override
    public List<Map<String, Object>> getDailyBreakdown(String month) {
        // 解析月份，null 则取当月
        LocalDate targetMonth;
        if (month == null || month.isEmpty()) {
            targetMonth = LocalDate.now().withDayOfMonth(1);
        } else {
            String[] parts = month.split("-");
            targetMonth = LocalDate.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 1);
        }
        LocalDateTime start = targetMonth.atStartOfDay();
        LocalDateTime end = targetMonth.plusMonths(1).atStartOfDay();
        int daysInMonth = targetMonth.lengthOfMonth();

        // 按日聚合营收（finance_record type=rental）
        QueryWrapper<FinanceRecord> revenueWrapper = new QueryWrapper<>();
        revenueWrapper.select("DATE(created_at) as day", "SUM(amount) as revenue")
                .eq("type", "rental")
                .between("created_at", start, end)
                .groupBy("DATE(created_at)")
                .orderByAsc("day");
        List<Map<String, Object>> revenueList = financeRecordMapper.selectMaps(revenueWrapper);
        Map<Integer, BigDecimal> revenueByDay = new HashMap<>();
        for (Map<String, Object> r : revenueList) {
            Object dayObj = r.get("day");
            int day = 1;
            if (dayObj instanceof java.sql.Date) {
                day = ((java.sql.Date) dayObj).toLocalDate().getDayOfMonth();
            } else if (dayObj instanceof LocalDate) {
                day = ((LocalDate) dayObj).getDayOfMonth();
            }
            revenueByDay.put(day, toBigDecimal(r.get("revenue")));
        }

        // 按日聚合手工成本（cost_record）
        QueryWrapper<CostRecord> costWrapper = new QueryWrapper<>();
        costWrapper.select("DATE(date) as day", "SUM(amount) as cost")
                .between("date", start.toLocalDate(), end.toLocalDate().minusDays(1))
                .groupBy("DATE(date)")
                .orderByAsc("day");
        List<Map<String, Object>> costList = costRecordMapper.selectMaps(costWrapper);
        Map<Integer, BigDecimal> manualCostByDay = new HashMap<>();
        for (Map<String, Object> c : costList) {
            Object dayObj = c.get("day");
            int day = 1;
            if (dayObj instanceof java.sql.Date) {
                day = ((java.sql.Date) dayObj).toLocalDate().getDayOfMonth();
            } else if (dayObj instanceof LocalDate) {
                day = ((LocalDate) dayObj).getDayOfMonth();
            }
            manualCostByDay.put(day, toBigDecimal(c.get("cost")));
        }

        // 按日聚合维保成本（car_maintenance）
        QueryWrapper<CarMaintenance> mtWrapper = new QueryWrapper<>();
        mtWrapper.select("DATE(maintenance_date) as day", "SUM(cost) as cost")
                .between("maintenance_date", start.toLocalDate(), end.toLocalDate().minusDays(1))
                .groupBy("DATE(maintenance_date)")
                .orderByAsc("day");
        List<Map<String, Object>> mtList = carMaintenanceMapper.selectMaps(mtWrapper);
        Map<Integer, BigDecimal> mtCostByDay = new HashMap<>();
        for (Map<String, Object> m : mtList) {
            Object dayObj = m.get("day");
            int day = 1;
            if (dayObj instanceof java.sql.Date) {
                day = ((java.sql.Date) dayObj).toLocalDate().getDayOfMonth();
            } else if (dayObj instanceof LocalDate) {
                day = ((LocalDate) dayObj).getDayOfMonth();
            }
            mtCostByDay.put(day, toBigDecimal(m.get("cost")));
        }

        // 按日聚合车辆租赁成本（订单 create_time 按日分组 × daily_cost × days）
        Map<Long, BigDecimal> carCostMap = carInfoMapper.selectList(null).stream()
                .collect(Collectors.toMap(CarInfo::getId,
                        c -> c.getDailyCost() == null ? BigDecimal.ZERO : c.getDailyCost(),
                        (a, b) -> a));
        LambdaQueryWrapper<CustomerOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.between(CustomerOrder::getCreateTime, start, end)
                .isNotNull(CustomerOrder::getCarId);
        List<CustomerOrder> orders = customerOrderMapper.selectList(orderWrapper);
        Map<Integer, BigDecimal> vehicleCostByDay = new HashMap<>();
        for (CustomerOrder order : orders) {
            if (order.getCreateTime() == null) continue;
            int day = order.getCreateTime().getDayOfMonth();
            BigDecimal dailyCost = carCostMap.getOrDefault(order.getCarId(), BigDecimal.ZERO);
            int d = order.getDays() == null ? 0 : order.getDays();
            vehicleCostByDay.merge(day, dailyCost.multiply(BigDecimal.valueOf(d)), BigDecimal::add);
        }

        // 组装每日数据（补全缺失天数）
        List<Map<String, Object>> result = new ArrayList<>();
        for (int d = 1; d <= daysInMonth; d++) {
            BigDecimal revenue = revenueByDay.getOrDefault(d, BigDecimal.ZERO);
            BigDecimal cost = manualCostByDay.getOrDefault(d, BigDecimal.ZERO)
                    .add(mtCostByDay.getOrDefault(d, BigDecimal.ZERO))
                    .add(vehicleCostByDay.getOrDefault(d, BigDecimal.ZERO));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", String.format("%s-%02d", month != null ? month : String.format("%04d-%02d", targetMonth.getYear(), targetMonth.getMonthValue()), d));
            row.put("day", d);
            row.put("revenue", revenue);
            row.put("cost", cost);
            row.put("profit", revenue.subtract(cost));
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getVehicleTypeBreakdown() {
        // 查询所有车辆，构建 carId -> (type, dailyCost) 映射
        List<CarInfo> cars = carInfoMapper.selectList(null);
        Map<Long, String> carTypeMap = new HashMap<>();
        Map<Long, BigDecimal> carCostMap = new HashMap<>();
        for (CarInfo car : cars) {
            carTypeMap.put(car.getId(), car.getType() != null ? car.getType() : "未分类");
            carCostMap.put(car.getId(), car.getDailyCost() == null ? BigDecimal.ZERO : car.getDailyCost());
        }

        // 查询所有 completed 订单，按车型聚合
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerOrder::getStatus, "completed");
        List<CustomerOrder> orders = customerOrderMapper.selectList(wrapper);

        Map<String, BigDecimal> revenueByType = new LinkedHashMap<>();
        Map<String, BigDecimal> costByType = new LinkedHashMap<>();
        Map<String, int[]> statsByType = new LinkedHashMap<>(); // [orderCount, rentalDays]

        for (CustomerOrder order : orders) {
            if (order.getCarId() == null) continue;
            String type = carTypeMap.getOrDefault(order.getCarId(), "未分类");
            BigDecimal revenue = order.getRentAmount() == null ? BigDecimal.ZERO : order.getRentAmount();
            BigDecimal dailyCost = carCostMap.getOrDefault(order.getCarId(), BigDecimal.ZERO);
            int days = order.getDays() == null ? 0 : order.getDays();
            BigDecimal cost = dailyCost.multiply(BigDecimal.valueOf(days));

            revenueByType.merge(type, revenue, BigDecimal::add);
            costByType.merge(type, cost, BigDecimal::add);
            statsByType.computeIfAbsent(type, k -> new int[2]);
            statsByType.get(type)[0] += 1;
            statsByType.get(type)[1] += days;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String type : revenueByType.keySet()) {
            BigDecimal revenue = revenueByType.get(type);
            BigDecimal cost = costByType.getOrDefault(type, BigDecimal.ZERO);
            BigDecimal profit = revenue.subtract(cost);
            BigDecimal profitMargin = BigDecimal.ZERO;
            if (revenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = profit.divide(revenue, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
            }
            int[] stats = statsByType.get(type);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("type", type);
            row.put("revenue", revenue);
            row.put("cost", cost);
            row.put("profit", profit);
            row.put("profitMargin", profitMargin);
            row.put("orderCount", stats[0]);
            row.put("rentalDays", stats[1]);
            result.add(row);
        }
        // 按营收降序
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return result;
    }

    /** Object 转 BigDecimal，处理 Integer/Long/BigDecimal 等类型 */
    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return new BigDecimal(obj.toString());
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
