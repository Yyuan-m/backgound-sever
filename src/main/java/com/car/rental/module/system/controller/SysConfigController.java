package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.module.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping
    @RequirePermission("settings:system")
    public Result<?> getAllConfigs() {
        return Result.ok(sysConfigService.getAllConfigs());
    }

    @PutMapping
    @RequirePermission("settings:system")
    public Result<?> saveConfigs(@RequestBody Map<String, String> configs) {
        sysConfigService.saveConfigs(configs);
        return Result.ok();
    }
}