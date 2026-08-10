package com.car.rental.module.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CustomerOrder;

public interface OrderService {

    IPage<CustomerOrder> getOrderList(long pageNum, long pageSize, String keyword, String status, String startDate, String endDate);

    CustomerOrder getOrderDetail(Long id);

    void addOrder(CustomerOrder order);

    void updateOrderStatus(Long id, String status);

    void updateOrder(Long id, CustomerOrder order);

    void deleteOrder(Long id);
}
