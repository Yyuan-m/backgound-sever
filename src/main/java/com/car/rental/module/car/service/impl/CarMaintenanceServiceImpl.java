package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarMaintenance;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.car.service.CarMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CarMaintenanceServiceImpl implements CarMaintenanceService {

    private final CarMaintenanceMapper carMaintenanceMapper;

    @Override
    public PageResult<CarMaintenance> list(Integer pageNum, Integer pageSize, Long vehicleId, String status) {
        Page<CarMaintenance> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<CarMaintenance> wrapper = new LambdaQueryWrapper<>();

        if (vehicleId != null) {
            wrapper.eq(CarMaintenance::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(CarMaintenance::getStatus, status);
        }
        wrapper.orderByDesc(CarMaintenance::getCreatedAt);

        IPage<CarMaintenance> result = carMaintenanceMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public CarMaintenance getById(Long id) {
        CarMaintenance maintenance = carMaintenanceMapper.selectById(id);
        if (maintenance == null) {
            throw new BusinessException("保养记录不存在");
        }
        return maintenance;
    }

    @Override
    public void add(CarMaintenance carMaintenance) {
        carMaintenanceMapper.insert(carMaintenance);
    }

    @Override
    public void update(Long id, CarMaintenance carMaintenance) {
        CarMaintenance existing = carMaintenanceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("保养记录不存在");
        }
        carMaintenance.setId(id);
        carMaintenanceMapper.updateById(carMaintenance);
    }

    @Override
    public void delete(Long id) {
        CarMaintenance existing = carMaintenanceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("保养记录不存在");
        }
        carMaintenanceMapper.deleteById(id);
    }
}