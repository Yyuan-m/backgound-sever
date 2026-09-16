package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.GpsTrack;
import com.car.rental.module.car.mapper.GpsTrackMapper;
import com.car.rental.module.car.service.GpsTrackService;
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

@Tag(name = "GPS轨迹", description = "车辆GPS定位轨迹的上报与查询，支持按车辆查历史轨迹与最新位置")
@RestController
@RequestMapping("/api/gps-track")
@RequiredArgsConstructor
public class GpsTrackController {

    private final GpsTrackService gpsTrackService;

    @Operation(summary = "GPS轨迹列表（分页）", description = "按上报时间倒序分页查询，可按车辆ID与状态筛选。需要 vehicle:gps 权限")
    @GetMapping("/list")
    @RequirePermission("vehicle:gps")
    public Result<PageResult<GpsTrack>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "车辆ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        PageResult<GpsTrack> pageResult = gpsTrackService.getList(pageNum, pageSize, vehicleId, status);
        return Result.ok(pageResult);
    }

    @Operation(summary = "车辆轨迹查询", description = "查询指定车辆的全部GPS轨迹记录，不分页。需要 vehicle:gps 权限")
    @GetMapping("/vehicle/{vehicleId}")
    @RequirePermission("vehicle:gps")
    public Result<List<GpsTrack>> getByVehicleId(@Parameter(description = "车辆ID") @PathVariable Long vehicleId) {
        return Result.ok(gpsTrackService.getByVehicleId(vehicleId));
    }

    @Operation(summary = "车辆最新位置", description = "查询指定车辆最新一条GPS轨迹记录。需要 vehicle:gps 权限")
    @GetMapping("/latest/{vehicleId}")
    @RequirePermission("vehicle:gps")
    public Result<GpsTrack> getLatest(@Parameter(description = "车辆ID") @PathVariable Long vehicleId) {
        return Result.ok(gpsTrackService.getLatest(vehicleId));
    }

    @Operation(summary = "新增轨迹记录", description = "上报一条GPS轨迹点（经纬度、速度、地址、时间戳等）。需要 vehicle:gps:add 权限")
    @PostMapping("/add")
    @RequirePermission("vehicle:gps:add")
    @LogChanges(
        entityClass = GpsTrack.class,
        mapperClass = GpsTrackMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "latitude:纬度",
            "longitude:经度", "speed:速度", "address:地址",
            "status:状态", "timestamp:时间戳"
        }
    )
    public Result<Void> add(@RequestBody GpsTrack gpsTrack) {
        gpsTrackService.add(gpsTrack);
        return Result.ok();
    }

    @Operation(summary = "编辑轨迹记录", description = "按ID更新轨迹信息，记录不存在则报错。需要 vehicle:gps:update 权限")
    @PutMapping("/update")
    @RequirePermission("vehicle:gps:update")
    @LogChanges(
        entityClass = GpsTrack.class,
        mapperClass = GpsTrackMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "latitude:纬度",
            "longitude:经度", "speed:速度", "address:地址",
            "status:状态", "timestamp:时间戳"
        }
    )
    public Result<Void> update(@RequestBody GpsTrack gpsTrack) {
        gpsTrackService.update(gpsTrack);
        return Result.ok();
    }

    @Operation(summary = "删除轨迹记录", description = "按ID删除轨迹记录，记录不存在则报错。需要 vehicle:gps:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:gps:delete")
    @LogChanges(
        entityClass = GpsTrack.class,
        mapperClass = GpsTrackMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "vehicleId:车辆ID", "vehicleName:车辆名称", "latitude:纬度",
            "longitude:经度", "speed:速度", "address:地址",
            "status:状态", "timestamp:时间戳"
        }
    )
    public Result<Void> delete(@Parameter(description = "轨迹记录ID") @PathVariable Long id) {
        gpsTrackService.delete(id);
        return Result.ok();
    }
}
