package com.car.rental.common.security;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.entity.OperationLog;
import com.car.rental.module.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 操作日志 AOP：自动记录所有写操作（POST/PUT/DELETE）到 operation_log 表。
 * <p>
 * 拦截所有 @RestController 类的方法，按 HTTP 方法注解（@PostMapping/@PutMapping/@DeleteMapping）过滤，
 * 仅记录写操作，避免对 GET 查询接口产生噪声。
 * 跳过 OperationLogController 自身，避免查询/导出日志时产生递归日志。
 * 日志写入失败不影响主流程，只会打印警告日志。
 * <p>
 * 优先级低于 PermissionAspect（其默认顺序），保证权限校验通过后再写入日志。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(100)
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    /** 用于获取 Mapper Bean（@LogChanges 字段变更对比时查询旧数据） */
    private final ApplicationContext applicationContext;

    /**
     * 拦截所有 @RestController 注解的类的方法。
     * 注意：使用 @within 而非 @annotation，可以覆盖没有 @RequirePermission 注解的接口
     *（如 AuthController.login/register/logout、UploadController、ProfileController 等）。
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object recordOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 跳过 OperationLogController 自身，避免查询/导出日志时产生递归日志
        String className = signature.getDeclaringType().getSimpleName();
        if ("OperationLogController".equals(className)) {
            return joinPoint.proceed();
        }

        // 仅记录写操作（带 @PostMapping / @PutMapping / @DeleteMapping 注解）
        if (!isWriteOperation(method)) {
            return joinPoint.proceed();
        }

        // 提取模块名（Controller 类名 → 中文模块名）
        String module = getModuleName(className);

        // 提取 action：方法名关键字 → 中文动作
        String methodName = method.getName();
        String action = getActionName(methodName);

        // 描述：中文动作 + 中文模块（如"新增车辆管理"）
        String description = action + module;

        // 字段变更对比：方法标注了 @LogChanges 时，按 mode 分支处理
        LogChanges logChanges = method.getAnnotation(LogChanges.class);
        Object oldEntity = null;
        Map<String, String> fieldLabelMap = null;
        Set<String> ignoreSet = null;
        if (logChanges != null) {
            fieldLabelMap = parseFieldLabels(logChanges.fieldLabels());
            ignoreSet = new HashSet<>(Arrays.asList(logChanges.ignoreFields()));
            // EDIT/DELETE 模式：proceed 前查旧数据（proceed 后数据已变更/删除，无法再查）
            // ADD 模式：无需查旧数据
            if (logChanges.mode() != LogChanges.Mode.ADD) {
                oldEntity = fetchOldEntity(joinPoint, logChanges);
            }
        }

        // 操作人：优先从 SecurityContextHolder 获取；登录/注册等未认证场景从方法参数中提取
        String operator = getCurrentUsername(joinPoint);

        // IP：从 HttpServletRequest 获取
        String ip = getRequestIp();

        long start = System.currentTimeMillis();
        Integer status = 1;
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            status = 0;
            throw e;
        } finally {
            try {
                // 按 mode 分支生成字段级描述
                if (logChanges != null) {
                    String detail = null;
                    switch (logChanges.mode()) {
                        case EDIT:
                            // 编辑：proceed 后从参数取新实体，与旧数据对比
                            if (oldEntity != null) {
                                detail = buildChangeDescription(oldEntity, joinPoint, logChanges, fieldLabelMap, ignoreSet);
                            }
                            break;
                        case ADD:
                            // 新增：proceed 后从参数取新实体，记录字段值
                            detail = buildSnapshotDescription(joinPoint, logChanges, fieldLabelMap, ignoreSet, false);
                            break;
                        case DELETE:
                            // 删除：记录 proceed 前查到的旧数据快照
                            if (oldEntity != null) {
                                detail = buildSnapshotDescriptionFromEntity(oldEntity, logChanges, fieldLabelMap, ignoreSet);
                            }
                            break;
                    }
                    if (detail != null && !detail.isEmpty()) {
                        description = description + "：" + detail;
                    }
                }
                OperationLog opLog = new OperationLog();
                opLog.setModule(module);
                opLog.setAction(action);
                opLog.setDescription(description);
                opLog.setOperator(operator);
                opLog.setIp(ip);
                opLog.setStatus(status);
                opLog.setCreatedAt(LocalDateTime.now());
                operationLogService.saveLog(opLog);
                log.debug("操作日志已记录：{}#{} ({}ms, status={})", module, action,
                        System.currentTimeMillis() - start, status);
            } catch (Exception ex) {
                log.warn("写入操作日志失败：{}", ex.getMessage());
            }
        }
        return result;
    }

    // ======================== 字段变更对比 ========================

    /** 解析 fieldLabels 数组为 Map<字段名, 中文名> */
    private Map<String, String> parseFieldLabels(String[] fieldLabels) {
        Map<String, String> map = new HashMap<>();
        if (fieldLabels == null) return map;
        for (String item : fieldLabels) {
            if (item == null) continue;
            int idx = item.indexOf(':');
            if (idx > 0) {
                map.put(item.substring(0, idx).trim(), item.substring(idx + 1).trim());
            }
        }
        return map;
    }

    /**
     * proceed 前查询旧数据：
     * 1. 先从方法参数中 entityClass 类型对象（新实体）的 id 字段取 id；
     * 2. 取不到时，从方法参数中找标注 @PathVariable 的 Long，或第一个 Long 参数
     *    （覆盖 update(@PathVariable Long id, @RequestBody CarInfo carInfo) 这种 id 在 path 里的场景）；
     * 3. 通过 ApplicationContext 获取 mapperClass 对应的 Mapper Bean，调用 selectById(id)。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object fetchOldEntity(ProceedingJoinPoint joinPoint, LogChanges logChanges) {
        try {
            Object newEntity = findEntityFromArgs(joinPoint.getArgs(), logChanges.entityClass());
            Object id = newEntity != null ? getIdValue(newEntity) : null;
            if (id == null) {
                id = extractIdFromMethodArgs(joinPoint);
            }
            if (id == null) return null;
            BaseMapper mapper = applicationContext.getBean(logChanges.mapperClass());
            return mapper.selectById((java.io.Serializable) id);
        } catch (Exception e) {
            log.warn("查询旧数据失败（@LogChanges）：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从方法参数中提取 id：优先 @PathVariable 标注的 Long，兜底第一个 Long 参数。
     * 用于 update(@PathVariable Long id, @RequestBody T entity) 这种 id 在 path 里、entity.id 尚未设置的接口。
     */
    private Object extractIdFromMethodArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        Object[] args = joinPoint.getArgs();
        java.lang.reflect.Parameter[] params = method.getParameters();
        java.lang.annotation.Annotation[][] paramAnnos = method.getParameterAnnotations();
        // 优先：@PathVariable 标注的 Long
        for (int i = 0; i < params.length && i < args.length; i++) {
            if (args[i] == null) continue;
            if (!Long.class.isAssignableFrom(params[i].getType()) && params[i].getType() != long.class) continue;
            for (java.lang.annotation.Annotation a : paramAnnos[i]) {
                if (a.annotationType() == org.springframework.web.bind.annotation.PathVariable.class) {
                    return args[i];
                }
            }
        }
        // 兜底：第一个非 null 的 Long 参数
        for (int i = 0; i < params.length && i < args.length; i++) {
            if (args[i] == null) continue;
            if (Long.class.isAssignableFrom(params[i].getType()) || params[i].getType() == long.class) {
                return args[i];
            }
        }
        return null;
    }

    /** 从方法参数中找指定类型的对象 */
    private Object findEntityFromArgs(Object[] args, Class<?> entityClass) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg != null && entityClass.isInstance(arg)) {
                return arg;
            }
        }
        return null;
    }

    /** 反射读取对象的 id 字段值 */
    private Object getIdValue(Object entity) {
        try {
            Field idField = findField(entity.getClass(), "id");
            if (idField == null) return null;
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    /** 沿继承链查找字段（实体可能继承基类） */
    private Field findField(Class<?> clazz, String name) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 对比新旧实体字段，生成变更描述。
     * 格式："字段A 由 [旧] 变更为 [新]；字段B 由 [旧] 变更为 [新]"
     * 仅记录值发生变化的字段，跳过 null、未变化、被忽略、非数据库字段的字段。
     */
    private String buildChangeDescription(Object oldEntity, ProceedingJoinPoint joinPoint,
                                          LogChanges logChanges, Map<String, String> fieldLabelMap,
                                          Set<String> ignoreSet) {
        Object newEntity = findEntityFromArgs(joinPoint.getArgs(), logChanges.entityClass());
        if (newEntity == null) return null;

        List<String> changes = new ArrayList<>();
        List<Field> fields = getAllFields(logChanges.entityClass());
        for (Field field : fields) {
            String fieldName = field.getName();
            // 跳过系统字段、被忽略字段、非数据库字段（@TableField(exist=false)）、静态字段
            if (ignoreSet.contains(fieldName) || "id".equals(fieldName)) continue;
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            TableField tf = field.getAnnotation(TableField.class);
            if (tf != null && !tf.exist()) continue;
            try {
                field.setAccessible(true);
                Object oldVal = field.get(oldEntity);
                Object newVal = field.get(newEntity);
                // 新值为 null 时，认为是前端未传该字段，不记录变更（避免误报）
                if (newVal == null) continue;
                if (equalsValue(oldVal, newVal)) continue;
                String label = fieldLabelMap.getOrDefault(fieldName, fieldName);
                changes.add(String.format("%s 由 [%s] 变更为 [%s]", label, display(oldVal), display(newVal)));
            } catch (IllegalAccessException ignored) {
                // 跳过无法访问的字段
            }
        }
        return changes.isEmpty() ? null : String.join("；", changes);
    }

    /** 收集类及其所有父类的 declared fields（去重） */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if (seen.add(f.getName())) {
                    list.add(f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    /**
     * ADD 模式：从方法参数中取新实体，记录所有非 null 字段的值。
     * 格式："字段A=值, 字段B=值"
     */
    private String buildSnapshotDescription(ProceedingJoinPoint joinPoint, LogChanges logChanges,
                                            Map<String, String> fieldLabelMap, Set<String> ignoreSet,
                                            boolean includeNull) {
        Object newEntity = findEntityFromArgs(joinPoint.getArgs(), logChanges.entityClass());
        if (newEntity == null) return null;
        return buildSnapshotDescriptionFromEntity(newEntity, logChanges, fieldLabelMap, ignoreSet);
    }

    /**
     * 通用快照描述：遍历实体所有字段，记录"字段中文名=值"。
     * 用于 ADD（新实体）和 DELETE（旧数据快照）。
     * 跳过 id、被忽略字段、非数据库字段（@TableField(exist=false)）、静态字段。
     */
    private String buildSnapshotDescriptionFromEntity(Object entity, LogChanges logChanges,
                                                     Map<String, String> fieldLabelMap, Set<String> ignoreSet) {
        List<String> parts = new ArrayList<>();
        List<Field> fields = getAllFields(logChanges.entityClass());
        for (Field field : fields) {
            String fieldName = field.getName();
            if (ignoreSet.contains(fieldName) || "id".equals(fieldName)) continue;
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            TableField tf = field.getAnnotation(TableField.class);
            if (tf != null && !tf.exist()) continue;
            try {
                field.setAccessible(true);
                Object val = field.get(entity);
                // 新增场景：跳过 null 字段（前端未传）；删除场景：也跳过 null（旧数据一般都有值）
                if (val == null) continue;
                String label = fieldLabelMap.getOrDefault(fieldName, fieldName);
                parts.add(label + "=" + display(val));
            } catch (IllegalAccessException ignored) {
            }
        }
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    /** 值相等判断（处理 BigDecimal 等比较） */
    private boolean equalsValue(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof java.math.BigDecimal && b instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) a).compareTo((java.math.BigDecimal) b) == 0;
        }
        return a.equals(b);
    }

    /** 显示值：null → 空，时间类型格式化，其他 toString */
    private String display(Object val) {
        if (val == null) return "";
        if (val instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) val).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (val instanceof java.time.LocalDate) {
            return val.toString();
        }
        return String.valueOf(val);
    }

    /**
     * 判断是否为写操作：方法上是否标注了 @PostMapping / @PutMapping / @DeleteMapping。
     * 这样比按方法名关键字判断更准确，能精确识别 HTTP 语义上的写操作。
     */
    private boolean isWriteOperation(Method method) {
        return method.isAnnotationPresent(PostMapping.class)
                || method.isAnnotationPresent(PutMapping.class)
                || method.isAnnotationPresent(DeleteMapping.class);
    }

    /** Controller 类名 → 中文模块名映射（与前端 MODULE_OPTIONS 对齐） */
    private String getModuleName(String controllerClassName) {
        switch (controllerClassName) {
            case "AuthController":
            case "ProfileController":
                return "认证";
            case "CarController":
            case "CarMaintenanceController":
            case "CarDocumentController":
            case "CarViolationController":
            case "CarImageController":
            case "GpsTrackController":
                return "车辆管理";
            case "OrderController":
                return "订单管理";
            case "CustomerController":
                return "租客管理";
            case "FinanceController":
            case "StatisticsController":
            case "CostController":
            case "InvoiceController":
            case "ReconciliationController":
                return "财务管理";
            case "AfterSalesComplaintController":
                return "售后工单";
            case "CouponController":
                return "营销活动";
            case "UserController":
            case "RoleController":
            case "MenuController":
                return "权限管理";
            case "DictController":
            case "CarouselController":
            case "AnnouncementController":
            case "SysConfigController":
            case "ThemeController":
            case "SysFileController":
                return "系统设置";
            case "UploadController":
                return "文件上传";
            default:
                // 兜底：去掉 Controller 后缀，转中划线
                String name = controllerClassName.replace("Controller", "");
                return name.isEmpty() ? "未知模块" : name;
        }
    }

    /** 方法名关键字 → 中文动作映射 */
    private String getActionName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return "操作";
        }
        String lower = methodName.toLowerCase();
        if (lower.contains("login") || lower.contains("logout")) {
            return lower.contains("logout") ? "登出" : "登录";
        }
        if (lower.contains("register")) {
            return "注册";
        }
        if (lower.contains("add") || lower.contains("create") || lower.contains("insert") || lower.contains("save")) {
            return "新增";
        }
        if (lower.contains("update") || lower.contains("edit") || lower.contains("modify") || lower.contains("change")) {
            return "编辑";
        }
        if (lower.contains("delete") || lower.contains("remove") || lower.contains("del")) {
            return "删除";
        }
        if (lower.contains("toggle") || lower.contains("disable") || lower.contains("enable") || lower.contains("status")) {
            return "切换状态";
        }
        if (lower.contains("handle")) {
            return "处理";
        }
        if (lower.contains("reset")) {
            return "重置";
        }
        if (lower.contains("batch")) {
            return "批量操作";
        }
        if (lower.contains("import")) {
            return "导入";
        }
        if (lower.contains("export")) {
            return "导出";
        }
        if (lower.contains("upload")) {
            return "上传";
        }
        if (lower.contains("permission")) {
            return "授权";
        }
        if (lower.contains("avatar")) {
            return "修改头像";
        }
        if (lower.contains("password")) {
            return "修改密码";
        }
        return "操作";
    }

    /**
     * 获取当前操作人用户名：
     * 1. 优先从 SecurityContextHolder 读取（已认证的常规接口）
     * 2. 兜底从方法参数中提取（登录/注册接口在调用时 SecurityContext 尚未建立，
     *    LoginDTO/RegisterDTO 的 username 字段可通过反射读取）
     */
    private String getCurrentUsername(ProceedingJoinPoint joinPoint) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                // JwtTokenFilter 中 principal 存的是 userId(Long)，真实 username 存放在 details 中
                Object details = auth.getDetails();
                if (details instanceof String && !((String) details).isEmpty()) {
                    return (String) details;
                }
                // 兜底：尝试 getName()（部分场景可能返回 username）
                return auth.getName();
            }
        } catch (Exception ignored) {
        }
        // 未登录场景（登录/注册接口）：尝试从方法参数中提取 username 字段
        String fromArgs = extractUsernameFromArgs(joinPoint.getArgs());
        if (fromArgs != null && !fromArgs.isEmpty()) {
            return fromArgs;
        }
        return "anonymous";
    }

    /** 反射读取方法参数中的 username 字段（用于登录/注册等未认证场景） */
    private String extractUsernameFromArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            try {
                // 仅对 DTO/Map 类型尝试读取 username 字段，避免对 String/Long 等基础类型反射
                if (arg instanceof java.util.Map<?, ?> map) {
                    Object v = map.get("username");
                    if (v != null) {
                        return String.valueOf(v);
                    }
                    continue;
                }
                // 跳过基础类型和常见框架对象
                String typeName = arg.getClass().getName();
                if (typeName.startsWith("java.") || typeName.startsWith("jakarta.")
                        || typeName.startsWith("org.springframework.")) {
                    continue;
                }
                java.lang.reflect.Field field = arg.getClass().getDeclaredField("username");
                field.setAccessible(true);
                Object v = field.get(arg);
                if (v != null) {
                    return String.valueOf(v);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // 当前参数没有 username 字段，继续尝试下一个
            }
        }
        return null;
    }

    private String getRequestIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 多级代理时取第一个
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        } catch (Exception ignored) {
        }
        return null;
    }
}
