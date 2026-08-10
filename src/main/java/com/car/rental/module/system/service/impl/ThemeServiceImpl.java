package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.entity.SysThemeConfig;
import com.car.rental.mapper.SysThemeConfigMapper;
import com.car.rental.module.system.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ThemeServiceImpl implements ThemeService {

    @Autowired
    private SysThemeConfigMapper sysThemeConfigMapper;

    @Override
    public SysThemeConfig getCurrentTheme(Long userId) {
        LambdaQueryWrapper<SysThemeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysThemeConfig::getUserId, userId);
        return sysThemeConfigMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveTheme(Long userId, String configJson) {
        LambdaQueryWrapper<SysThemeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysThemeConfig::getUserId, userId);
        SysThemeConfig existingConfig = sysThemeConfigMapper.selectOne(wrapper);

        if (existingConfig != null) {
            existingConfig.setConfigJson(configJson);
            existingConfig.setUpdatedAt(LocalDateTime.now());
            sysThemeConfigMapper.updateById(existingConfig);
        } else {
            SysThemeConfig config = new SysThemeConfig();
            config.setUserId(userId);
            config.setConfigJson(configJson);
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
            sysThemeConfigMapper.insert(config);
        }
    }
}