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
}
