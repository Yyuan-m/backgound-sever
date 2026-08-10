package com.car.rental.module.car.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.GpsTrack;
import com.car.rental.module.car.mapper.GpsTrackMapper;
import com.car.rental.module.car.service.GpsTrackService;
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
@RequestMapping("/api/gps-track")
@RequiredArgsConstructor
public class GpsTrackController {

    private final GpsTrackService gpsTrackService;

    @GetMapping("/list")
    @RequirePermission("vehicle:gps")
    public Result<PageResult<GpsTrack>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String status) {
        PageResult<GpsTrack> pageResult = gpsTrackService.getList(pageNum, pageSize, vehicleId, status);
        return Result.ok(pageResult);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @RequirePermission("vehicle:gps")
    public Result<List<GpsTrack>> getByVehicleId(@PathVariable Long vehicleId) {
        return Result.ok(gpsTrackService.getByVehicleId(vehicleId));
    }

    @GetMapping("/latest/{vehicleId}")
    @RequirePermission("vehicle:gps")
    public Result<GpsTrack> getLatest(@PathVariable Long vehicleId) {
        return Result.ok(gpsTrackService.getLatest(vehicleId));
    }

    @PostMapping("/add")
    @RequirePermission("vehicle:gps")
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

    @PutMapping("/update")
    @RequirePermission("vehicle:gps")
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

    @DeleteMapping("/{id}")
    @RequirePermission("vehicle:gps")
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
    public Result<Void> delete(@PathVariable Long id) {
        gpsTrackService.delete(id);
        return Result.ok();
    }
}