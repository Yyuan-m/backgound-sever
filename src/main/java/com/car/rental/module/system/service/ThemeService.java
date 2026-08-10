package com.car.rental.module.system.service;

import com.car.rental.entity.SysThemeConfig;

public interface ThemeService {

    SysThemeConfig getCurrentTheme(Long userId);

    void saveTheme(Long userId, String configJson);
}