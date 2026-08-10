package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarInfo;

public interface CarService {

    PageResult<CarInfo> list(Integer pageNum, Integer pageSize, String keyword, String type, String status);

    CarInfo detail(Long id);

    void add(CarInfo carInfo);

    void update(Long id, CarInfo carInfo);

    void delete(Long id);

    void updateStatus(Long id, String status);
}