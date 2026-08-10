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
import java.util.Collection;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

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

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(a -> {
                    String auth = a.getAuthority();
                    // 超级管理员通配符
                    if (auth.equals("*")) return true;
                    // 精确匹配
                    if (auth.equals(requiredPermission)) return true;
                    // 层级匹配：拥有 "order" 权限则自动拥有 "order:list"、"order:detail" 等子权限
                    if (requiredPermission.contains(":") && requiredPermission.startsWith(auth + ":")) return true;
                    return false;
                });

        if (!hasPermission) {
            log.warn("用户 {} 缺少权限: {}", authentication.getName(), requiredPermission);
            throw new BusinessException(Result.FORBIDDEN, "权限不足，需要权限: " + requiredPermission);
        }

        return joinPoint.proceed();
    }
}