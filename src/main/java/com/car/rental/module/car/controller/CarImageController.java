package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarImage;
import com.car.rental.module.car.mapper.CarImageMapper;
import com.car.rental.module.car.service.CarImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/car/image")
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;

    @GetMapping("/list")
    @RequirePermission("vehicle:list")
    public Result<PageResult<CarImage>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String category) {
        PageResult<CarImage> pageResult = carImageService.list(pageNum, pageSize, vehicleId, category);
        return Result.ok(pageResult);
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:add")
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

    @DeleteMapping("/delete/{id}")
    @RequirePermission("vehicle:delete")
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
    public Result<Void> delete(@PathVariable Long id) {
        carImageService.delete(id);
        return Result.ok();
    }
}
