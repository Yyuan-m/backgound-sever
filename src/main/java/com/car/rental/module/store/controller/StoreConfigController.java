package com.car.rental.module.store.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.Result;
import com.car.rental.entity.City;
import com.car.rental.entity.Store;
import com.car.rental.module.store.service.StoreConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 门店配置控制器（城市 + 门店 CRUD）
 * 管理端接口需要登录权限，公开接口 /api/public/city-store-tree 供 C 端官网调用
 */
@RestController
@RequiredArgsConstructor
public class StoreConfigController {

    private final StoreConfigService storeConfigService;

    // ===== 城市 CRUD =====

    @GetMapping("/api/store-config/city/list")
    @RequirePermission("settings")
    public Result<List<City>> cityList() {
        return Result.ok(storeConfigService.getCityList());
    }

    @PostMapping("/api/store-config/city/add")
    @RequirePermission("settings")
    public Result<Void> addCity(@RequestBody City city) {
        storeConfigService.addCity(city);
        return Result.ok();
    }

    @PutMapping("/api/store-config/city/update/{id}")
    @RequirePermission("settings")
    public Result<Void> updateCity(@PathVariable Long id, @RequestBody City city) {
        storeConfigService.updateCity(id, city);
        return Result.ok();
    }

    @DeleteMapping("/api/store-config/city/delete/{id}")
    @RequirePermission("settings")
    public Result<Void> deleteCity(@PathVariable Long id) {
        storeConfigService.deleteCity(id);
        return Result.ok();
    }

    @PutMapping("/api/store-config/city/status/{id}")
    @RequirePermission("settings")
    public Result<Void> toggleCityStatus(@PathVariable Long id, @RequestParam Integer status) {
        storeConfigService.toggleCityStatus(id, status);
        return Result.ok();
    }

    // ===== 门店 CRUD =====

    @GetMapping("/api/store-config/store/list")
    @RequirePermission("settings")
    public Result<List<Store>> storeList(@RequestParam(required = false) Long cityId) {
        return Result.ok(storeConfigService.getStoreList(cityId));
    }

    @PostMapping("/api/store-config/store/add")
    @RequirePermission("settings")
    public Result<Void> addStore(@RequestBody Store store) {
        storeConfigService.addStore(store);
        return Result.ok();
    }

    @PutMapping("/api/store-config/store/update/{id}")
    @RequirePermission("settings")
    public Result<Void> updateStore(@PathVariable Long id, @RequestBody Store store) {
        storeConfigService.updateStore(id, store);
        return Result.ok();
    }

    @DeleteMapping("/api/store-config/store/delete/{id}")
    @RequirePermission("settings")
    public Result<Void> deleteStore(@PathVariable Long id) {
        storeConfigService.deleteStore(id);
        return Result.ok();
    }

    @PutMapping("/api/store-config/store/status/{id}")
    @RequirePermission("settings")
    public Result<Void> toggleStoreStatus(@PathVariable Long id, @RequestParam Integer status) {
        storeConfigService.toggleStoreStatus(id, status);
        return Result.ok();
    }

    // ===== 公开接口（C端官网查询，无需登录） =====

    @GetMapping("/api/public/city-store-tree")
    public Result<List<Map<String, Object>>> publicCityStoreTree() {
        return Result.ok(storeConfigService.getPublicCityStoreTree());
    }
}
