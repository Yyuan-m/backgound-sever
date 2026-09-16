package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarViolation;
import com.car.rental.module.car.mapper.CarViolationMapper;
import com.car.rental.module.car.service.CarViolationService;
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

import java.util.Map;

@Tag(name = "车辆违章管理", description = "车辆违章记录的登记、编辑与处理，关联订单与客户信息")
@RestController
@RequestMapping("/api/car-violation")
@RequiredArgsConstructor
public class CarViolationController {

    private final CarViolationService carViolationService;

    @Operation(summary = "违章列表（分页）", description = "按创建时间倒序分页查询，可按车辆、订单、违章类型、状态筛选，自动填充关联订单编号与客户姓名。需要 vehicle:violation 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:violation")
    public Result<PageResult<CarViolation>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "车辆ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "订单ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "违章类型") @RequestParam(required = false) String violationType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        PageResult<CarViolation> pageResult = carViolationService.getList(pageNum, pageSize, vehicleId, orderId, violationType, status);
        return Result.ok(pageResult);
    }

    @Operation(summary = "违章详情", description = "按ID查询违章记录，含关联订单编号与客户姓名。需要 vehicle:violation 权限")
    @GetMapping("/{id}")
    @RequirePermission("vehicle:violation")
    public Result<CarViolation> getById(@Parameter(description = "违章记录ID") @PathVariable Long id) {
        return Result.ok(carViolationService.getById(id));
    }

    @Operation(summary = "新增违章记录", description = "登记违章信息（类型、时间、地点、罚款金额、扣分等，可关联订单）。需要 vehicle:violation:add 权限")
    @PostMapping("/add")
    @RequirePermission("vehicle:violation:add")
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

    @Operation(summary = "编辑违章记录", description = "按ID更新违章信息，记录不存在则报错。需要 vehicle:violation:update 权限")
    @PutMapping("/update")
    @RequirePermission("vehicle:violation:update")
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

    @Operation(summary = "处理违章", description = "更新违章状态与处理人；处理时间前端传值优先（格式 yyyy-MM-dd），未传或解析失败时取当前时间。需要 vehicle:violation:handle 权限")
    @PutMapping("/{id}/handle")
    @RequirePermission("vehicle:violation:handle")
    public Result<Void> handle(@Parameter(description = "违章记录ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = (String) params.get("status");
        String handler = (String) params.get("handler");
        String handleDate = (String) params.get("handleDate");
        carViolationService.handle(id, status, handler, handleDate);
        return Result.ok();
    }

    @Operation(summary = "删除违章记录", description = "按ID删除违章记录，记录不存在则报错。需要 vehicle:violation:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:violation:delete")
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
    public Result<Void> delete(@Parameter(description = "违章记录ID") @PathVariable Long id) {
        carViolationService.delete(id);
        return Result.ok();
    }
}
