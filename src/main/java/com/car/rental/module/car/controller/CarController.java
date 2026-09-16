package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.service.CarService;
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

@Tag(name = "车辆管理", description = "车辆信息增删改查、上下架、预约情况查询；出租状态由订单按当天日期实时计算")
@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @Operation(summary = "车辆列表（分页）", description = "车辆状态实时计算：当天处于待支付/租赁中订单租期内的显示 rented（租赁中），未来预约不算当天在租；reservedCount 为未来预约数（>0 即已预约）。需要 vehicle:list 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:list")
    public Result<PageResult<CarInfo>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：车名/品牌/车牌号模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "车型（字典 vehicle_type）") @RequestParam(required = false) String type,
            @Parameter(description = "车辆状态筛选（idle 空闲 / rented 租赁中 / offline 已下架，基于实时计算结果过滤）") @RequestParam(required = false) String status) {
        PageResult<CarInfo> pageResult = carService.list(pageNum, pageSize, keyword, type, status);
        return Result.ok(pageResult);
    }

    @Operation(summary = "车辆详情", description = "含一对一车辆配置 carConfig 与全部素材 materials（只读）。需要 vehicle:detail 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("vehicle:detail")
    public Result<CarInfo> detail(@Parameter(description = "车辆ID") @PathVariable Long id) {
        CarInfo carInfo = carService.detail(id);
        return Result.ok(carInfo);
    }

    @Operation(summary = "新增车辆", description = "车牌号唯一校验；可一并传入 carConfig 车辆配置；日成本价不填时自动=日租×0.54。需要 vehicle:add 权限")
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

    @Operation(summary = "编辑车辆", description = "车牌号唯一校验；编辑时 status（业务状态）由订单实时计算，提交的 status 会被后端忽略。需要 vehicle:update 权限")
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

    @Operation(summary = "删除车辆", description = "逻辑删除，同时删除关联的车辆配置。需要 vehicle:delete 权限")
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

    @Operation(summary = "车辆上架/下架", description = "仅允许 idle（上架）/offline（下架）两个值，其他值报错；出租状态由订单自动计算不可手动设置。需要 vehicle:status 权限")
    @PutMapping("/status/{id}")
    @RequirePermission("vehicle:status")
    public Result<Void> updateStatus(@Parameter(description = "车辆ID") @PathVariable Long id,
                                     @Parameter(description = "目标状态：idle 上架 / offline 下架") @RequestParam String status) {
        carService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 车辆预约情况：未结束订单列表（当前租赁中 + 未来预约），
     * 含预约时间段与客户信息，用于列表"预约"按钮弹窗展示
     */
    @Operation(summary = "车辆预约情况", description = "查该车全部未结束订单（待支付/租赁中 且 还车日>=今天），按开始日期升序，含客户姓名电话与租期。需要 vehicle:detail 权限")
    @GetMapping("/reservations/{id}")
    @RequirePermission("vehicle:detail")
    public Result<List<CustomerOrder>> reservations(@Parameter(description = "车辆ID") @PathVariable Long id) {
        return Result.ok(carService.listReservations(id));
    }
}