package com.car.rental.module.statistics.service;

import java.util.List;
import java.util.Map;

/**
 * 财务统计聚合服务
 * 从 finance_record / cost_record / car_maintenance 等业务数据自动聚合，
 * 避免各 tab 数据孤岛、与订单/车辆数据脱节的问题。
 */
public interface FinanceStatsService {

    /**
     * 财务总览：总营收 / 总成本 / 净利润 + 本月/本年维度
     */
    Map<String, Object> getFinanceOverview();

    /**
     * 利润分析：按月聚合营收、成本、净利润趋势（最近 N 个月）
     */
    List<Map<String, Object>> getProfitTrend(int months);

    /**
     * 对账数据：按月从 finance_record 自动聚合
     * 返回租金收入 / 其他费用 / 净收入，并标记是否已对账
     */
    List<Map<String, Object>> getReconciliationAgggregate(int months);

    /**
     * 车辆成本参考表：列出所有车辆的日租/日成本/利润率/累计租赁天数/累计成本
     */
    List<Map<String, Object>> getVehicleCostReference();

    /**
     * 成本构成饼图：车辆租赁成本 / 维保成本 / 手工成本占比
     * @param period total/month/year
     */
    List<Map<String, Object>> getCostComposition(String period);

    /**
     * 每日收支：指定月份按日聚合收入和成本
     * @param month 格式 yyyy-MM，null 表示当月
     */
    List<Map<String, Object>> getDailyBreakdown(String month);

    /**
     * 车型收支分析：按车辆类型分组聚合收入/成本/利润/利润率
     */
    List<Map<String, Object>> getVehicleTypeBreakdown();
}
