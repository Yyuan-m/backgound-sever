package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.CustomerOrder;

import java.util.List;

public interface CarService {

    PageResult<CarInfo> list(Integer pageNum, Integer pageSize, String keyword, String type, String status);

    CarInfo detail(Long id);

    void add(CarInfo carInfo);

    void update(Long id, CarInfo carInfo);

    void delete(Long id);

    void updateStatus(Long id, String status);

    /**
     * 查询车辆的预约情况：未结束订单（pending/renting 且 end_date >= 今天），
     * 按开始日期升序，包含当前租赁中 + 未来预约，用于"预约情况"弹窗展示
     */
    List<CustomerOrder> listReservations(Long carId);
}