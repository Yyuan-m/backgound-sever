package com.car.rental.module.store.service;

import com.car.rental.entity.City;
import com.car.rental.entity.Store;

import java.util.List;
import java.util.Map;

/**
 * 门店配置服务（城市 + 门店 CRUD）
 */
public interface StoreConfigService {

    // ===== 城市 =====
    List<City> getCityList();
    void addCity(City city);
    void updateCity(Long id, City city);
    void deleteCity(Long id);
    void toggleCityStatus(Long id, Integer status);

    // ===== 门店 =====
    List<Store> getStoreList(Long cityId);
    void addStore(Store store);
    void updateStore(Long id, Store store);
    void deleteStore(Long id);
    void toggleStoreStatus(Long id, Integer status);

    // ===== 公开接口（C端官网查询） =====
    /**
     * 获取城市-门店树（仅返回启用状态的数据）
     * @return [{ id, name, children: [{ id, name, address, phone }] }]
     */
    List<Map<String, Object>> getPublicCityStoreTree();
}
