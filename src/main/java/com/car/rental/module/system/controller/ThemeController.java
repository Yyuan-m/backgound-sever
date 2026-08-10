package com.car.rental.module.system.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.SysThemeConfig;
import com.car.rental.module.system.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system/theme")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/current")
    public Result<SysThemeConfig> current() {
        Long userId = securityUtil.getCurrentUserId();
        SysThemeConfig config = themeService.getCurrentTheme(userId);
        return Result.ok(config);
    }

    @PutMapping("/save")
    public Result<Void> save(@RequestBody Map<String, String> body) {
        Long userId = securityUtil.getCurrentUserId();
        String configJson = body.get("configJson");
        themeService.saveTheme(userId, configJson);
        return Result.ok();
    }
}