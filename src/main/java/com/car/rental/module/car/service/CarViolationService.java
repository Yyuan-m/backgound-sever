package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarViolation;

public interface CarViolationService {

    PageResult<CarViolation> getList(Integer pageNum, Integer pageSize, Long vehicleId, Long orderId, String violationType, String status);

    CarViolation getById(Long id);

    void add(CarViolation carViolation);

    void update(CarViolation carViolation);

    void handle(Long id, String status, String handler, String handleDate);

    void delete(Long id);
}