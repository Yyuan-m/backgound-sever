package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarMaintenance;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.car.service.CarMaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "车辆维保管理", description = "车辆保养/维修记录的登记与跟踪，含费用、里程与下次保养日期")
@RestController
@RequestMapping("/api/car-maintenance")
@RequiredArgsConstructor
public class CarMaintenanceController {

    private final CarMaintenanceService carMaintenanceService;

    @Operation(summary = "维保列表（分页）", description = "按创建时间倒序分页查询，可按车辆ID与状态筛选。需要 vehicle:maintenance 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:maintenance")
    public Result<PageResult<CarMaintenance>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "车辆ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        PageResult<CarMaintenance> pageResult = carMaintenanceService.list(pageNum, pageSize, vehicleId, status);
        return Result.ok(pageResult);
    }

    @Operation(summary = "维保详情", description = "按ID查询维保记录，记录不存在则报错。需要 vehicle:maintenance 权限")
    @GetMapping("/{id}")
    @RequirePermission("vehicle:maintenance")
    public Result<CarMaintenance> getById(@Parameter(description = "维保记录ID") @PathVariable Long id) {
        return Result.ok(carMaintenanceService.getById(id));
    }

    @Operation(summary = "新增维保记录", description = "登记保养/维修信息（类型、费用、里程、保养公司、下次保养日期等）。需要 vehicle:maintenance:add 权限")
    @PostMapping("/add")
    @RequirePermission("vehicle:maintenance:add")
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

    @Operation(summary = "编辑维保记录", description = "按ID更新维保信息，记录不存在则报错。需要 vehicle:maintenance:update 权限")
    @PutMapping("/update/{id}")
    @RequirePermission("vehicle:maintenance:update")
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
    public Result<Void> update(@Parameter(description = "维保记录ID") @PathVariable Long id, @RequestBody CarMaintenance carMaintenance) {
        carMaintenanceService.update(id, carMaintenance);
        return Result.ok();
    }

    @Operation(summary = "删除维保记录", description = "按ID删除维保记录，记录不存在则报错。需要 vehicle:maintenance:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:maintenance:delete")
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
    public Result<Void> delete(@Parameter(description = "维保记录ID") @PathVariable Long id) {
        carMaintenanceService.delete(id);
        return Result.ok();
    }
}
