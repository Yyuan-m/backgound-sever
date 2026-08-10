package com.car.rental.module.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;

public interface CustomerService {

    IPage<CustomerInfo> getCustomerList(long pageNum, long pageSize, String keyword, Integer status, String level);

    void updateCustomer(Long id, CustomerInfo customer);

    void deleteCustomer(Long id);

    void toggleStatus(Long id, Integer status);

    IPage<CustomerOrder> getCustomerOrders(Long memberId, long pageNum, long pageSize);

    CustomerInfo getById(Long id);

    /**
     * 实名认证：检查 member 表的身份证信息是否齐全（身份证号 + 正反面照），
     * 若齐全则自动将 customer_info.real_name_status 更新为 1（已认证）
     */
    void verifyRealName(Long memberId);
}