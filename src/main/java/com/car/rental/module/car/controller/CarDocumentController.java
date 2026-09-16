package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarDocument;
import com.car.rental.module.car.mapper.CarDocumentMapper;
import com.car.rental.module.car.service.CarDocumentService;
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

import java.util.List;

@Tag(name = "车辆证件管理", description = "车辆行驶证、保险等证件档案的增删改查与到期状态管理")
@RestController
@RequestMapping("/api/car-document")
@RequiredArgsConstructor
public class CarDocumentController {

    private final CarDocumentService carDocumentService;

    @Operation(summary = "证件列表（分页）", description = "按创建时间倒序分页查询，可按车辆ID、证件类型、状态筛选。需要 vehicle:document 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:document")
    public Result<PageResult<CarDocument>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "车辆ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "证件类型") @RequestParam(required = false) String docType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        PageResult<CarDocument> pageResult = carDocumentService.getList(pageNum, pageSize, vehicleId, docType, status);
        return Result.ok(pageResult);
    }

    @Operation(summary = "证件详情", description = "按ID查询证件信息，证件不存在则报错。需要 vehicle:document 权限")
    @GetMapping("/{id}")
    @RequirePermission("vehicle:document")
    public Result<CarDocument> getById(@Parameter(description = "证件ID") @PathVariable Long id) {
        return Result.ok(carDocumentService.getById(id));
    }

    @Operation(summary = "车辆证件查询", description = "查询指定车辆的全部证件，不分页，按创建时间倒序。需要 vehicle:document 权限")
    @GetMapping("/vehicle/{vehicleId}")
    @RequirePermission("vehicle:document")
    public Result<List<CarDocument>> getByVehicleId(@Parameter(description = "车辆ID") @PathVariable Long vehicleId) {
        return Result.ok(carDocumentService.getByVehicleId(vehicleId));
    }

    @Operation(summary = "新增证件", description = "登记车辆证件（类型、编号、发证/到期日期、证件图片等）。需要 vehicle:document:add 权限")
    @PostMapping("/add")
    @RequirePermission("vehicle:document:add")
    @LogChanges(
        entityClass = CarDocument.class,
        mapperClass = CarDocumentMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "docType:证件类型",
            "docNumber:证件编号", "issueDate:发证日期", "expireDate:到期日期",
            "status:状态", "imageUrl:证件图片", "remark:备注"
        }
    )
    public Result<Void> add(@RequestBody CarDocument carDocument) {
        carDocumentService.add(carDocument);
        return Result.ok();
    }

    @Operation(summary = "编辑证件", description = "按ID更新证件信息，证件不存在则报错。需要 vehicle:document:update 权限")
    @PutMapping("/update")
    @RequirePermission("vehicle:document:update")
    @LogChanges(
        entityClass = CarDocument.class,
        mapperClass = CarDocumentMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "docType:证件类型",
            "docNumber:证件编号", "issueDate:发证日期", "expireDate:到期日期",
            "status:状态", "imageUrl:证件图片", "remark:备注"
        }
    )
    public Result<Void> update(@RequestBody CarDocument carDocument) {
        carDocumentService.update(carDocument);
        return Result.ok();
    }

    @Operation(summary = "删除证件", description = "按ID删除证件记录，证件不存在则报错。需要 vehicle:document:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:document:delete")
    @LogChanges(
        entityClass = CarDocument.class,
        mapperClass = CarDocumentMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "docType:证件类型",
            "docNumber:证件编号", "issueDate:发证日期", "expireDate:到期日期",
            "status:状态", "imageUrl:证件图片", "remark:备注"
        }
    )
    public Result<Void> delete(@Parameter(description = "证件ID") @PathVariable Long id) {
        carDocumentService.delete(id);
        return Result.ok();
    }
}
