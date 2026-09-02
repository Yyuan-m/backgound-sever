package com.car.rental.common.security;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    /** 按钮级权限动作词：权限标识末段为这些词的均为操作权限，必须精确拥有（与前端 ACTION_WORDS 一致） */
    private static final Set<String> ACTION_WORDS = new HashSet<>(Arrays.asList(
            "add", "update", "delete", "status", "handle", "process",
            "restore", "reset-password", "export", "import"));

    private final ButtonPermissionService buttonPermissionService;

    @Around("@annotation(com.car.rental.common.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        String requiredPermission = requirePermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(Result.UNAUTHORIZED, "未登录或登录已过期");
        }

        // 被管理员禁用的按钮权限：对所有用户（含超级管理员）立即失效
        if (buttonPermissionService.getDisabledButtonPerms().contains(requiredPermission)) {
            log.warn("权限 {} 对应的按钮已被禁用，拒绝访问", requiredPermission);
            throw new BusinessException(Result.FORBIDDEN, "您没有该操作的权限，请联系管理员配置");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 按钮级权限必须精确匹配：勾选菜单不代表自动拥有其下按钮权限。
        // 判定规则（与前端 useAuthStore.hasPermission 一致）：
        // 1) sys_menu 中 type='button' 的权限标识；2) 权限标识 ≥3 段（如 vehicle:maintenance:add）；
        // 3) 末段为动作词（如 vehicle:add、order:delete、feedback:process）
        String[] permParts = requiredPermission.split(":");
        boolean strict = buttonPermissionService.isButtonPermission(requiredPermission)
                || permParts.length >= 3
                || ACTION_WORDS.contains(permParts[permParts.length - 1]);
        boolean hasPermission = authorities.stream()
                .anyMatch(a -> {
                    String auth = a.getAuthority();
                    // 超级管理员通配符
                    if (auth.equals("*")) return true;
                    // 精确匹配
                    if (auth.equals(requiredPermission)) return true;
                    // 层级匹配：拥有 "order" 权限则自动拥有 "order:list"、"order:detail" 等子权限
                    // （仅对菜单级权限生效，按钮级权限见上方 strict 判断）
                    if (!strict && requiredPermission.contains(":") && requiredPermission.startsWith(auth + ":")) return true;
                    return false;
                });

        if (!hasPermission) {
            log.warn("用户 {} 缺少权限: {}", authentication.getName(), requiredPermission);
            throw new BusinessException(Result.FORBIDDEN, "您没有该操作的权限，请联系管理员配置");
        }

        return joinPoint.proceed();
    }
}