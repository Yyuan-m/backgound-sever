package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarImage;
import com.car.rental.module.car.mapper.CarImageMapper;
import com.car.rental.module.car.service.CarImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "车辆素材管理", description = "车辆图片素材的分页查询与增删，按车辆与分类管理")
@RestController
@RequestMapping("/api/car/image")
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;

    @Operation(summary = "素材列表（分页）", description = "按创建时间倒序分页查询，可按车辆ID与图片分类筛选。需要 vehicle:image 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:image")
    public Result<PageResult<CarImage>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "车辆ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "图片分类") @RequestParam(required = false) String category) {
        PageResult<CarImage> pageResult = carImageService.list(pageNum, pageSize, vehicleId, category);
        return Result.ok(pageResult);
    }

    @Operation(summary = "新增素材", description = "车辆ID与图片地址必填；车辆名称未填时自动带出；状态未填默认 1 启用。需要 vehicle:image:add 权限")
    @PostMapping("/add")
    @RequirePermission("vehicle:image:add")
    @LogChanges(
        entityClass = CarImage.class,
        mapperClass = CarImageMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "category:图片分类",
            "url:图片地址", "status:状态"
        }
    )
    public Result<Void> add(@RequestBody CarImage carImage) {
        carImageService.add(carImage);
        return Result.ok();
    }

    @Operation(summary = "删除素材", description = "按ID删除素材记录，素材不存在则报错。需要 vehicle:image:delete 权限")
    @DeleteMapping("/delete/{id}")
    @RequirePermission("vehicle:image:delete")
    @LogChanges(
        entityClass = CarImage.class,
        mapperClass = CarImageMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "category:图片分类",
            "url:图片地址", "status:状态"
        }
    )
    public Result<Void> delete(@Parameter(description = "素材ID") @PathVariable Long id) {
        carImageService.delete(id);
        return Result.ok();
    }
}
