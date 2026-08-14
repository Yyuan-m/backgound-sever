package com.car.rental.module.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.City;
import com.car.rental.entity.Store;
import com.car.rental.module.store.mapper.CityMapper;
import com.car.rental.module.store.mapper.StoreMapper;
import com.car.rental.module.store.service.StoreConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreConfigServiceImpl implements StoreConfigService {

    private final CityMapper cityMapper;
    private final StoreMapper storeMapper;

    // ===== 城市 =====

    @Override
    public List<City> getCityList() {
        return cityMapper.selectList(
                new LambdaQueryWrapper<City>()
                        .orderByAsc(City::getSort)
                        .orderByAsc(City::getId));
    }

    @Override
    public void addCity(City city) {
        // 校验名称唯一
        Long count = cityMapper.selectCount(
                new LambdaQueryWrapper<City>().eq(City::getName, city.getName()));
        if (count > 0) {
            throw new BusinessException("城市名称已存在");
        }
        city.setId(null);
        if (city.getStatus() == null) city.setStatus(1);
        if (city.getSort() == null) city.setSort(0);
        city.setCreatedAt(LocalDateTime.now());
        city.setUpdatedAt(LocalDateTime.now());
        cityMapper.insert(city);
    }

    @Override
    public void updateCity(Long id, City city) {
        City existing = cityMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("城市不存在");
        }
        // 名称唯一性校验（排除自身）
        if (city.getName() != null && !city.getName().equals(existing.getName())) {
            Long count = cityMapper.selectCount(
                    new LambdaQueryWrapper<City>()
                            .eq(City::getName, city.getName())
                            .ne(City::getId, id));
            if (count > 0) {
                throw new BusinessException("城市名称已存在");
            }
        }
        existing.setName(city.getName() != null ? city.getName() : existing.getName());
        existing.setSort(city.getSort() != null ? city.getSort() : existing.getSort());
        existing.setStatus(city.getStatus() != null ? city.getStatus() : existing.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        cityMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteCity(Long id) {
        City existing = cityMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("城市不存在");
        }
        // 删除城市下的所有门店
        storeMapper.delete(new LambdaQueryWrapper<Store>().eq(Store::getCityId, id));
        cityMapper.deleteById(id);
    }

    @Override
    public void toggleCityStatus(Long id, Integer status) {
        City existing = cityMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("城市不存在");
        }
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        cityMapper.updateById(existing);
    }

    // ===== 门店 =====

    @Override
    public List<Store> getStoreList(Long cityId) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>()
                .orderByAsc(Store::getSort)
                .orderByAsc(Store::getId);
        if (cityId != null) {
            wrapper.eq(Store::getCityId, cityId);
        }
        return storeMapper.selectList(wrapper);
    }

    @Override
    public void addStore(Store store) {
        if (store.getCityId() == null) {
            throw new BusinessException("请选择所属城市");
        }
        City city = cityMapper.selectById(store.getCityId());
        if (city == null) {
            throw new BusinessException("所属城市不存在");
        }
        store.setId(null);
        if (store.getStatus() == null) store.setStatus(1);
        if (store.getSort() == null) store.setSort(0);
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        storeMapper.insert(store);
    }

    @Override
    public void updateStore(Long id, Store store) {
        Store existing = storeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("门店不存在");
        }
        if (store.getCityId() != null) {
            City city = cityMapper.selectById(store.getCityId());
            if (city == null) {
                throw new BusinessException("所属城市不存在");
            }
            existing.setCityId(store.getCityId());
        }
        existing.setName(store.getName() != null ? store.getName() : existing.getName());
        existing.setAddress(store.getAddress() != null ? store.getAddress() : existing.getAddress());
        existing.setPhone(store.getPhone() != null ? store.getPhone() : existing.getPhone());
        existing.setSort(store.getSort() != null ? store.getSort() : existing.getSort());
        existing.setStatus(store.getStatus() != null ? store.getStatus() : existing.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        storeMapper.updateById(existing);
    }

    @Override
    public void deleteStore(Long id) {
        Store existing = storeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("门店不存在");
        }
        storeMapper.deleteById(id);
    }

    @Override
    public void toggleStoreStatus(Long id, Integer status) {
        Store existing = storeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("门店不存在");
        }
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        storeMapper.updateById(existing);
    }

    // ===== 公开接口（C端官网查询） =====

    @Override
    public List<Map<String, Object>> getPublicCityStoreTree() {
        // 查询启用的城市
        List<City> cities = cityMapper.selectList(
                new LambdaQueryWrapper<City>()
                        .eq(City::getStatus, 1)
                        .orderByAsc(City::getSort)
                        .orderByAsc(City::getId));
        if (cities.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> cityIds = cities.stream().map(City::getId).collect(Collectors.toList());

        // 查询这些城市下启用的门店
        List<Store> stores = storeMapper.selectList(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getStatus, 1)
                        .in(Store::getCityId, cityIds)
                        .orderByAsc(Store::getSort)
                        .orderByAsc(Store::getId));

        // 按城市ID分组
        Map<Long, List<Store>> storeMap = stores.stream()
                .collect(Collectors.groupingBy(Store::getCityId));

        // 组装树
        List<Map<String, Object>> result = new ArrayList<>();
        for (City city : cities) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", city.getId());
            node.put("name", city.getName());
            List<Map<String, Object>> children = new ArrayList<>();
            List<Store> cityStores = storeMap.getOrDefault(city.getId(), new ArrayList<>());
            for (Store s : cityStores) {
                Map<String, Object> child = new LinkedHashMap<>();
                child.put("id", s.getId());
                child.put("name", s.getName());
                child.put("address", s.getAddress());
                child.put("phone", s.getPhone());
                children.add(child);
            }
            node.put("children", children);
            result.add(node);
        }
        return result;
    }
}
