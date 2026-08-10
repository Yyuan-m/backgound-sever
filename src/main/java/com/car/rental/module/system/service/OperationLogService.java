package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.OperationLog;

import java.util.List;

public interface OperationLogService {

    IPage<OperationLog> getList(Integer pageNum, Integer pageSize, String module, String action, String operator, Integer status);

    OperationLog getById(Long id);

    /** 异步写入操作日志（不会抛出异常影响主流程） */
    void saveLog(OperationLog log);

    /**
     * 查询需要导出的日志列表：
     * - 当 ids 非空时，按 ids 精确导出（忽略其他筛选条件）
     * - 当 ids 为空时，按筛选条件导出全部（不分页）
     *
     * @param ids      选中的日志 ID 列表（可为 null 或空）
     * @param module   模块名（模糊匹配）
     * @param action   操作类型（模糊匹配）
     * @param operator 操作人（模糊匹配）
     * @param status   状态（1成功 / 0失败）
     * @return 日志列表（按创建时间倒序）
     */
    List<OperationLog> getForExport(List<Long> ids, String module, String action, String operator, Integer status);
}
