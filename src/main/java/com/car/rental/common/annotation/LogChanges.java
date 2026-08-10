package com.car.rental.common.annotation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在 Controller 的写操作方法上，让 OperationLogAspect 自动记录字段级信息。
 * <p>
 * 三种模式：
 * 1. EDIT（默认）：proceed 前查旧数据，proceed 后对比新旧字段，生成"字段A 由 [旧] 变更为 [新]"；
 * 2. ADD：proceed 后从参数取新实体，记录"字段A=值, 字段B=值"（仅非 null 字段）；
 * 3. DELETE：proceed 前查旧数据，proceed 后记录旧数据快照"字段A=值, 字段B=值"。
 * <p>
 * 示例：
 * <pre>
 * // 编辑（对比新旧）
 * &#64;LogChanges(entityClass = CarInfo.class, mapperClass = CarInfoMapper.class,
 *     fieldLabels = {"dailyPrice:日租金", "status:状态"})
 * public Result&lt;Void&gt; update(@PathVariable Long id, @RequestBody CarInfo carInfo) { ... }
 *
 * // 新增（记录新值）
 * &#64;LogChanges(entityClass = CarInfo.class, mapperClass = CarInfoMapper.class,
 *     mode = LogChanges.Mode.ADD)
 * public Result&lt;Void&gt; add(@RequestBody CarInfo carInfo) { ... }
 *
 * // 删除（记录旧快照）
 * &#64;LogChanges(entityClass = CarInfo.class, mapperClass = CarInfoMapper.class,
 *     mode = LogChanges.Mode.DELETE)
 * public Result&lt;Void&gt; delete(@PathVariable Long id) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogChanges {

    /** 实体类（用于反射取字段、id、对比值） */
    Class<?> entityClass();

    /** 实体对应的 MyBatis-Plus Mapper，必须继承 BaseMapper */
    Class<? extends BaseMapper<?>> mapperClass();

    /** 操作模式：EDIT 编辑对比 / ADD 新增记录 / DELETE 删除快照 */
    Mode mode() default Mode.EDIT;

    /**
     * 字段名 → 中文名映射，格式 "字段名:中文名"，例如 "dailyPrice:日租金"。
     * 未配置的字段将直接使用字段名（驼峰）作为描述。
     */
    String[] fieldLabels() default {};

    /** 需要忽略对比的字段名（如 createdAt、updatedAt、isDelete 等系统字段） */
    String[] ignoreFields() default {};

    /** 操作模式枚举 */
    enum Mode {
        /** 编辑：对比新旧字段，生成变更描述 */
        EDIT,
        /** 新增：记录新实体的字段值 */
        ADD,
        /** 删除：记录删除前旧实体的字段快照 */
        DELETE
    }
}
