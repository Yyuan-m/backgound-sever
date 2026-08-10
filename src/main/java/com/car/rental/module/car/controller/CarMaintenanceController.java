package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarMaintenance;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.car.service.CarMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/car-maintenance")
@RequiredArgsConstructor
public class CarMaintenanceController {

    private final CarMaintenanceService carMaintenanceService;

    @GetMapping("/list")
    @RequirePermission("vehicle:maintenance")
    public Result<PageResult<CarMaintenance>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String status) {
        PageResult<CarMaintenance> pageResult = carMaintenanceService.list(pageNum, pageSize, vehicleId, status);
        return Result.ok(pageResult);
    }

    @GetMapping("/{id}")
    @RequirePermission("vehicle:maintenance")
    public Result<CarMaintenance> getById(@PathVariable Long id) {
        return Result.ok(carMaintenanceService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:maintenance")
    @LogChanges(
        entityClass = CarMaintenance.class,
        mapperClass = CarMaintenanceMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "type:保养类型",
            "description:保养描述", "cost:费用", "mileage:里程",
            "company:保养公司", "status:状态", "maintenanceDate:保养日期",
            "nextDate:下次保养日期", "remark:备注"
        }
    )
    public Result<Void> add(@RequestBody CarMaintenance carMaintenance) {
        carMaintenanceService.add(carMaintenance);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    @RequirePermission("vehicle:maintenance")
    @LogChanges(
        entityClass = CarMaintenance.class,
        mapperClass = CarMaintenanceMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "type:保养类型",
            "description:保养描述", "cost:费用", "mileage:里程",
            "company:保养公司", "status:状态", "maintenanceDate:保养日期",
            "nextDate:下次保养日期", "remark:备注"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody CarMaintenance carMaintenance) {
        carMaintenanceService.update(id, carMaintenance);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:maintenance")
    @LogChanges(
        entityClass = CarMaintenance.class,
        mapperClass = CarMaintenanceMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "type:保养类型",
            "description:保养描述", "cost:费用", "mileage:里程",
            "company:保养公司", "status:状态", "maintenanceDate:保养日期",
            "nextDate:下次保养日期", "remark:备注"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        carMaintenanceService.delete(id);
        return Result.ok();
    }
}