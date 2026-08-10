package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarViolation;
import com.car.rental.module.car.mapper.CarViolationMapper;
import com.car.rental.module.car.service.CarViolationService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/car-violation")
@RequiredArgsConstructor
public class CarViolationController {

    private final CarViolationService carViolationService;

    @GetMapping("/list")
    @RequirePermission("vehicle:violation")
    public Result<PageResult<CarViolation>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String violationType,
            @RequestParam(required = false) String status) {
        PageResult<CarViolation> pageResult = carViolationService.getList(pageNum, pageSize, vehicleId, orderId, violationType, status);
        return Result.ok(pageResult);
    }

    @GetMapping("/{id}")
    @RequirePermission("vehicle:violation")
    public Result<CarViolation> getById(@PathVariable Long id) {
        return Result.ok(carViolationService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:violation")
    @LogChanges(
        entityClass = CarViolation.class,
        mapperClass = CarViolationMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "orderId:订单ID",
            "violationType:违章类型", "violationDate:违章时间", "location:违章地点",
            "fineAmount:罚款金额", "points:扣分", "status:状态",
            "handler:处理人", "handleDate:处理时间", "remark:备注"
        }
    )
    public Result<Void> add(@RequestBody CarViolation carViolation) {
        carViolationService.add(carViolation);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequirePermission("vehicle:violation")
    @LogChanges(
        entityClass = CarViolation.class,
        mapperClass = CarViolationMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "orderId:订单ID",
            "violationType:违章类型", "violationDate:违章时间", "location:违章地点",
            "fineAmount:罚款金额", "points:扣分", "status:状态",
            "handler:处理人", "handleDate:处理时间", "remark:备注"
        }
    )
    public Result<Void> update(@RequestBody CarViolation carViolation) {
        carViolationService.update(carViolation);
        return Result.ok();
    }

    @PutMapping("/{id}/handle")
    @RequirePermission("vehicle:violation")
    public Result<Void> handle(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = (String) params.get("status");
        String handler = (String) params.get("handler");
        String handleDate = (String) params.get("handleDate");
        carViolationService.handle(id, status, handler, handleDate);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:violation")
    @LogChanges(
        entityClass = CarViolation.class,
        mapperClass = CarViolationMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "orderId:订单ID",
            "violationType:违章类型", "violationDate:违章时间", "location:违章地点",
            "fineAmount:罚款金额", "points:扣分", "status:状态",
            "handler:处理人", "handleDate:处理时间", "remark:备注"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        carViolationService.delete(id);
        return Result.ok();
    }
}