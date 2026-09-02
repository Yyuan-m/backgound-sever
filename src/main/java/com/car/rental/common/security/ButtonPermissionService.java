package com.car.rental.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.car.rental.entity.SysMenu;
import com.car.rental.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 按钮权限服务：加载 sys_menu 中 type='button' 的权限标识集合。
 * <p>
 * 按钮权限必须精确匹配——角色勾选了菜单（如 vehicle）不代表自动拥有其下按钮权限
 * （如 vehicle:delete），必须在角色权限配置中单独勾选；
 * 而菜单级权限仍保留 PermissionAspect 的层级匹配，兼容存量角色数据。
 * <p>
 * 按钮集合在内存中缓存，菜单数据变更（新增/修改/删除/启停）时由 MenuServiceImpl 主动失效。
 */
@Service
@RequiredArgsConstructor
public class ButtonPermissionService {

    public static final String TYPE_BUTTON = "button";

    private final SysMenuMapper sysMenuMapper;

    private volatile Set<String> buttonPerms;

    /** 被禁用（status=0）的按钮权限集合：从用户有效权限中剔除 */
    private volatile Set<String> disabledButtonPerms;

    /** 判断某权限标识是否为「按钮级」权限（需要精确匹配） */
    public boolean isButtonPermission(String permission) {
        return permission != null && getButtonPerms().contains(permission);
    }

    /** 获取被禁用的按钮权限集合，菜单数据变更后自动重建 */
    public Set<String> getDisabledButtonPerms() {
        Set<String> local = disabledButtonPerms;
        if (local == null) {
            synchronized (this) {
                local = disabledButtonPerms;
                if (local == null) {
                    LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SysMenu::getType, TYPE_BUTTON).eq(SysMenu::getStatus, 0);
                    Set<String> set = new HashSet<>();
                    for (SysMenu menu : sysMenuMapper.selectList(wrapper)) {
                        String perm = menu.getPermission();
                        if (perm != null && !perm.isBlank()) {
                            set.add(perm);
                        }
                    }
                    disabledButtonPerms = local = set;
                }
            }
        }
        return local;
    }

    public Set<String> getButtonPerms() {
        Set<String> local = buttonPerms;
        if (local == null) {
            synchronized (this) {
                local = buttonPerms;
                if (local == null) {
                    LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SysMenu::getType, TYPE_BUTTON);
                    Set<String> set = new HashSet<>();
                    for (SysMenu menu : sysMenuMapper.selectList(wrapper)) {
                        String perm = menu.getPermission();
                        if (perm != null && !perm.isBlank()) {
                            set.add(perm);
                        }
                    }
                    buttonPerms = local = set;
                }
            }
        }
        return local;
    }

    /** 菜单数据变更后调用，重建缓存 */
    public void evictCache() {
        buttonPerms = null;
        disabledButtonPerms = null;
    }
}
