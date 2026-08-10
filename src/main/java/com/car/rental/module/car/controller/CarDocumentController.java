package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarDocument;
import com.car.rental.module.car.mapper.CarDocumentMapper;
import com.car.rental.module.car.service.CarDocumentService;
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

@RestController
@RequestMapping("/api/car-document")
@RequiredArgsConstructor
public class CarDocumentController {

    private final CarDocumentService carDocumentService;

    @GetMapping("/list")
    @RequirePermission("vehicle:document")
    public Result<PageResult<CarDocument>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String docType,
            @RequestParam(required = false) String status) {
        PageResult<CarDocument> pageResult = carDocumentService.getList(pageNum, pageSize, vehicleId, docType, status);
        return Result.ok(pageResult);
    }

    @GetMapping("/{id}")
    @RequirePermission("vehicle:document")
    public Result<CarDocument> getById(@PathVariable Long id) {
        return Result.ok(carDocumentService.getById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @RequirePermission("vehicle:document")
    public Result<List<CarDocument>> getByVehicleId(@PathVariable Long vehicleId) {
        return Result.ok(carDocumentService.getByVehicleId(vehicleId));
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:document")
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

    @PutMapping("/update")
    @RequirePermission("vehicle:document")
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

    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:document")
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
    public Result<Void> delete(@PathVariable Long id) {
        carDocumentService.delete(id);
        return Result.ok();
    }
}