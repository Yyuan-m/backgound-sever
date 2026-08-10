package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarMaintenance;

public interface CarMaintenanceService {

    PageResult<CarMaintenance> list(Integer pageNum, Integer pageSize, Long vehicleId, String status);

    CarMaintenance getById(Long id);

    void add(CarMaintenance carMaintenance);

    void update(Long id, CarMaintenance carMaintenance);

    void delete(Long id);
}