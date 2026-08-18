package com.car.rental.module.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CustomerOrder;

import java.util.Map;

public interface OrderService {

    IPage<CustomerOrder> getOrderList(long pageNum, long pageSize, String keyword, String status, String startDate, String endDate);

    CustomerOrder getOrderDetail(Long id);

    void addOrder(CustomerOrder order);

    void updateOrderStatus(Long id, String status);

    void updateOrder(Long id, CustomerOrder order);

    void deleteOrder(Long id);

    /**
     * 按状态统计订单数量（全量，不参与分页/筛选条件）
     * @return status → count 映射，未出现的状态不包含在 map 中
     */
    Map<String, Long> getStatusCount();

    /**
     * 自动完成到期订单：status=renting 且 end_date < 今天 → completed
     * 与 C 端 autoCompleteOrders 对称，走管理后台 updateOrderStatus 流程，
     * 确保发票 + 财务流水（rental 收入 / rental_cost 支出）一并生成。
     * @return 本次自动完成的订单数量
     */
    int autoCompleteExpiredOrders();

    /**
     * 回补缺失财务流水 + 发票：扫描所有 status=completed 但 finance_record 中缺少
     * rental 类型流水的订单，补生成收入/支出流水与发票（幂等）。
     * 用于修复 C 端自动完成（绕过管理后台）等场景下遗漏的财务数据。
     * @return 本次回补的订单数量
     */
    int backfillMissingFinanceRecords();
}
