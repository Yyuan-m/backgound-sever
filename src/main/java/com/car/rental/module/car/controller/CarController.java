package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarInfo;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.service.CarService;
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
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/list")
    @RequirePermission("vehicle:list")
    public Result<PageResult<CarInfo>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        PageResult<CarInfo> pageResult = carService.list(pageNum, pageSize, keyword, type, status);
        return Result.ok(pageResult);
    }

    @GetMapping("/detail/{id}")
    @RequirePermission("vehicle:detail")
    public Result<CarInfo> detail(@PathVariable Long id) {
        CarInfo carInfo = carService.detail(id);
        return Result.ok(carInfo);
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:add")
    @LogChanges(
        entityClass = CarInfo.class,
        mapperClass = CarInfoMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "brand:品牌", "series:车系", "type:类型",
            "plateNumber:车牌号", "vin:车架号", "engineNo:发动机号",
            "registrationDate:登记日期", "mileage:里程", "displacement:排量",
            "seats:座位数", "color:颜色", "conditionLevel:车况",
            "originalValue:原值", "residualValue:残值", "status:状态",
            "dailyPrice:日租金", "halfDayPrice:半日租金", "nightPrice:夜间租金",
            "weeklyDiscount:周折扣", "monthlyDiscount:月折扣",
            "holidaySurcharge:节假日加价", "overtimePerHour:超时费",
            "remoteReturnFee:异地还车费", "images:图片", "description:描述",
            "tags:标签", "isHot:是否热门", "isRecommend:是否推荐"
        }
    )
    public Result<Void> add(@RequestBody CarInfo carInfo) {
        carService.add(carInfo);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    @RequirePermission("vehicle:update")
    @LogChanges(
        entityClass = CarInfo.class,
        mapperClass = CarInfoMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "brand:品牌", "series:车系", "type:类型",
            "plateNumber:车牌号", "vin:车架号", "engineNo:发动机号",
            "registrationDate:登记日期", "mileage:里程", "displacement:排量",
            "seats:座位数", "color:颜色", "conditionLevel:车况",
            "originalValue:原值", "residualValue:残值", "status:状态",
            "dailyPrice:日租金", "halfDayPrice:半日租金", "nightPrice:夜间租金",
            "weeklyDiscount:周折扣", "monthlyDiscount:月折扣",
            "holidaySurcharge:节假日加价", "overtimePerHour:超时费",
            "remoteReturnFee:异地还车费", "images:图片", "description:描述",
            "tags:标签", "isHot:是否热门", "isRecommend:是否推荐"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody CarInfo carInfo) {
        carService.update(id, carInfo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission("vehicle:delete")
    @LogChanges(
        entityClass = CarInfo.class,
        mapperClass = CarInfoMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "name:名称", "brand:品牌", "series:车系", "type:类型",
            "plateNumber:车牌号", "vin:车架号", "engineNo:发动机号",
            "registrationDate:登记日期", "mileage:里程", "displacement:排量",
            "seats:座位数", "color:颜色", "conditionLevel:车况",
            "originalValue:原值", "residualValue:残值", "status:状态",
            "dailyPrice:日租金", "halfDayPrice:半日租金", "nightPrice:夜间租金",
            "weeklyDiscount:周折扣", "monthlyDiscount:月折扣",
            "holidaySurcharge:节假日加价", "overtimePerHour:超时费",
            "remoteReturnFee:异地还车费", "images:图片", "description:描述",
            "tags:标签", "isHot:是否热门", "isRecommend:是否推荐"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    @RequirePermission("vehicle:status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        carService.updateStatus(id, status);
        return Result.ok();
    }
}