package com.car.rental.module.system.service;

import com.car.rental.entity.SysConfig;

import java.util.List;
import java.util.Map;

public interface SysConfigService {

    Map<String, String> getAllConfigs();

    List<SysConfig> getConfigsByCategory(String category);

    void saveConfigs(Map<String, String> configs);
}