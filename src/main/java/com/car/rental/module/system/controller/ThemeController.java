package com.car.rental.module.system.controller;

import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.SysThemeConfig;
import com.car.rental.module.system.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "主题管理", description = "当前登录用户的个性化主题配置（configJson）查询与保存")
@RestController
@RequestMapping("/api/system/theme")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private SecurityUtil securityUtil;

    @Operation(summary = "当前用户主题配置", description = "说明：返回当前登录用户的主题配置，未设置过时返回 null；仅需登录，无需专门权限")
    @GetMapping("/current")
    public Result<SysThemeConfig> current() {
        Long userId = securityUtil.getCurrentUserId();
        SysThemeConfig config = themeService.getCurrentTheme(userId);
        return Result.ok(config);
    }

    @Operation(summary = "保存主题配置", description = "说明：body 传 configJson，已有配置则更新、没有则新建；仅需登录，无需专门权限")
    @PutMapping("/save")
    public Result<Void> save(@RequestBody Map<String, String> body) {
        Long userId = securityUtil.getCurrentUserId();
        String configJson = body.get("configJson");
        themeService.saveTheme(userId, configJson);
        return Result.ok();
    }
}