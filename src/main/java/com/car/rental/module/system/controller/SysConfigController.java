package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.module.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "系统配置", description = "系统参数配置（键值对形式）的查询与批量保存")
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "查询全部系统配置", description = "说明：返回所有系统配置项（键值对）。需要 settings:system 权限")
    @GetMapping
    @RequirePermission("settings:system")
    public Result<?> getAllConfigs() {
        return Result.ok(sysConfigService.getAllConfigs());
    }

    @Operation(summary = "保存系统配置", description = "说明：批量保存配置键值对。需要 settings:system:update 权限")
    @PutMapping
    @RequirePermission("settings:system:update")
    public Result<?> saveConfigs(@RequestBody Map<String, String> configs) {
        sysConfigService.saveConfigs(configs);
        return Result.ok();
    }
}
