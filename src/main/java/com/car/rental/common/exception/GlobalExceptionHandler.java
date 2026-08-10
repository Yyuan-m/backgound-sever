package com.car.rental.common.exception;

import com.car.rental.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private boolean isDev() {
        return "dev".equalsIgnoreCase(activeProfile) || "local".equalsIgnoreCase(activeProfile);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.error(Result.FORBIDDEN, "权限不足");
    }

    /**
     * 数据库相关异常：表不存在、字段映射错误等。
     * 开发环境透出具体错误信息，便于快速定位；
     * 生产环境返回通用提示，避免泄漏数据库结构。
     */
    @ExceptionHandler({java.sql.SQLException.class, org.springframework.dao.DataAccessException.class})
    public Result<Void> handleDataAccessException(Exception e) {
        log.error("数据库异常: ", e);
        String msg = isDev() ? ("数据库异常: " + rootMessage(e)) : "数据库访问异常，请检查表结构或联系管理员";
        return Result.error(Result.ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        String msg = isDev() ? ("系统内部错误: " + rootMessage(e)) : "系统内部错误";
        return Result.error(Result.ERROR, msg);
    }

    /** 提取异常根因消息 */
    private static String rootMessage(Throwable e) {
        Throwable cur = e;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        return msg != null ? msg : e.getClass().getSimpleName();
    }
}