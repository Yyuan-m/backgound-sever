package com.car.rental.module.store.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.entity.City;
import com.car.rental.entity.Store;
import com.car.rental.module.store.service.StoreConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 门店配置控制器（城市 + 门店 CRUD）
 * 管理端接口需要登录权限，公开接口 /api/public/city-store-tree 供 C 端官网调用
 */
@Tag(name = "门店配置", description = "城市与门店的增删改查、启用停用管理，含 C 端官网城市-门店树公开查询")
@RestController
@RequiredArgsConstructor
public class StoreConfigController {

    private final StoreConfigService storeConfigService;

    // ===== 城市 CRUD =====

    @Operation(summary = "城市列表", description = "查询全部城市配置。需要 settings 权限")
    @GetMapping("/api/store-config/city/list")
    @RequirePermission("settings")
    public Result<List<City>> cityList() {
        return Result.ok(storeConfigService.getCityList());
    }

    @Operation(summary = "新增城市", description = "新增城市配置。需要 settings:store:add 权限")
    @PostMapping("/api/store-config/city/add")
    @RequirePermission("settings:store:add")
    public Result<Void> addCity(@RequestBody City city) {
        storeConfigService.addCity(city);
        return Result.ok();
    }

    @Operation(summary = "编辑城市", description = "按 ID 更新城市配置。需要 settings:store:update 权限")
    @PutMapping("/api/store-config/city/update/{id}")
    @RequirePermission("settings:store:update")
    public Result<Void> updateCity(@Parameter(description = "城市ID") @PathVariable Long id, @RequestBody City city) {
        storeConfigService.updateCity(id, city);
        return Result.ok();
    }

    @Operation(summary = "删除城市", description = "按 ID 删除城市配置。需要 settings:store:delete 权限")
    @DeleteMapping("/api/store-config/city/delete/{id}")
    @RequirePermission("settings:store:delete")
    public Result<Void> deleteCity(@Parameter(description = "城市ID") @PathVariable Long id) {
        storeConfigService.deleteCity(id);
        return Result.ok();
    }

    @Operation(summary = "城市启用/停用", description = "按 ID 切换城市状态，影响 C 端城市-门店树展示。需要 settings:store:status 权限")
    @PutMapping("/api/store-config/city/status/{id}")
    @RequirePermission("settings:store:status")
    public Result<Void> toggleCityStatus(@Parameter(description = "城市ID") @PathVariable Long id, @Parameter(description = "目标状态：1 启用 / 0 禁用") @RequestParam Integer status) {
        storeConfigService.toggleCityStatus(id, status);
        return Result.ok();
    }

    // ===== 门店 CRUD =====

    @Operation(summary = "门店列表", description = "查询门店配置，可按城市 ID 过滤。需要 settings 权限")
    @GetMapping("/api/store-config/store/list")
    @RequirePermission("settings")
    public Result<List<Store>> storeList(@Parameter(description = "城市ID（可选，按城市过滤）") @RequestParam(required = false) Long cityId) {
        return Result.ok(storeConfigService.getStoreList(cityId));
    }

    @Operation(summary = "新增门店", description = "新增门店配置。需要 settings:store:add 权限")
    @PostMapping("/api/store-config/store/add")
    @RequirePermission("settings:store:add")
    public Result<Void> addStore(@RequestBody Store store) {
        storeConfigService.addStore(store);
        return Result.ok();
    }

    @Operation(summary = "编辑门店", description = "按 ID 更新门店配置。需要 settings:store:update 权限")
    @PutMapping("/api/store-config/store/update/{id}")
    @RequirePermission("settings:store:update")
    public Result<Void> updateStore(@Parameter(description = "门店ID") @PathVariable Long id, @RequestBody Store store) {
        storeConfigService.updateStore(id, store);
        return Result.ok();
    }

    @Operation(summary = "删除门店", description = "按 ID 删除门店配置。需要 settings:store:delete 权限")
    @DeleteMapping("/api/store-config/store/delete/{id}")
    @RequirePermission("settings:store:delete")
    public Result<Void> deleteStore(@Parameter(description = "门店ID") @PathVariable Long id) {
        storeConfigService.deleteStore(id);
        return Result.ok();
    }

    @Operation(summary = "门店启用/停用", description = "按 ID 切换门店状态，影响 C 端城市-门店树展示。需要 settings:store:status 权限")
    @PutMapping("/api/store-config/store/status/{id}")
    @RequirePermission("settings:store:status")
    public Result<Void> toggleStoreStatus(@Parameter(description = "门店ID") @PathVariable Long id, @Parameter(description = "目标状态：1 启用 / 0 禁用") @RequestParam Integer status) {
        storeConfigService.toggleStoreStatus(id, status);
        return Result.ok();
    }

    // ===== 公开接口（C端官网查询，无需登录） =====

    @Operation(summary = "城市-门店树（公开接口）", description = "供 C 端官网调用，返回启用状态的城市-门店树（城市仅含 id/name，门店含 id/name/address/phone），免登录")
    @GetMapping("/api/public/city-store-tree")
    public Result<List<Map<String, Object>>> publicCityStoreTree() {
        return Result.ok(storeConfigService.getPublicCityStoreTree());
    }
}
