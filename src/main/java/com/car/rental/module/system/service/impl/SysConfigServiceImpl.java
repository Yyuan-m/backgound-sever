package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.entity.SysConfig;
import com.car.rental.mapper.SysConfigMapper;
import com.car.rental.module.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, String> getAllConfigs() {
        List<SysConfig> configs = sysConfigMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (SysConfig config : configs) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    @Override
    public List<SysConfig> getConfigsByCategory(String category) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getCategory, category);
        return sysConfigMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveConfigs(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, entry.getKey());
            SysConfig existing = sysConfigMapper.selectOne(wrapper);
            if (existing != null) {
                existing.setConfigValue(entry.getValue());
                existing.setUpdatedAt(LocalDateTime.now());
                sysConfigMapper.updateById(existing);
            } else {
                SysConfig newConfig = new SysConfig();
                newConfig.setConfigKey(entry.getKey());
                newConfig.setConfigValue(entry.getValue());
                newConfig.setCreatedAt(LocalDateTime.now());
                newConfig.setUpdatedAt(LocalDateTime.now());
                sysConfigMapper.insert(newConfig);
            }
        }
    }
}