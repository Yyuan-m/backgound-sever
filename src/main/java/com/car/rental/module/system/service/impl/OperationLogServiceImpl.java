package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.OperationLog;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.mapper.OperationLogMapper;
import com.car.rental.module.system.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<OperationLog> getList(Integer pageNum, Integer pageSize, String module, String action, String operator, Integer status) {
        Page<OperationLog> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.like(OperationLog::getModule, module);
        }
        if (StringUtils.hasText(action)) {
            wrapper.like(OperationLog::getAction, action);
        }
        if (StringUtils.hasText(operator)) {
            wrapper.like(OperationLog::getOperator, operator);
        }
        if (status != null) {
            wrapper.eq(OperationLog::getStatus, status);
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        IPage<OperationLog> result = operationLogMapper.selectPage(page, wrapper);
        fillOperatorName(result.getRecords());
        return result;
    }

    @Override
    public OperationLog getById(Long id) {
        OperationLog log = operationLogMapper.selectById(id);
        if (log == null) {
            throw new BusinessException("操作日志不存在");
        }
        fillOperatorName(List.of(log));
        return log;
    }

    @Override
    public void saveLog(OperationLog log) {
        if (log == null) {
            return;
        }
        try {
            if (log.getCreatedAt() == null) {
                log.setCreatedAt(LocalDateTime.now());
            }
            operationLogMapper.insert(log);
        } catch (Exception e) {
            // 日志写入失败不影响主流程
            org.slf4j.LoggerFactory.getLogger(OperationLogServiceImpl.class)
                    .warn("写入操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public List<OperationLog> getForExport(List<Long> ids, String module, String action, String operator, Integer status) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        // ids 非空时按 ids 精确导出，忽略其他筛选
        if (ids != null && !ids.isEmpty()) {
            wrapper.in(OperationLog::getId, ids);
        } else {
            if (StringUtils.hasText(module)) {
                wrapper.like(OperationLog::getModule, module);
            }
            if (StringUtils.hasText(action)) {
                wrapper.like(OperationLog::getAction, action);
            }
            if (StringUtils.hasText(operator)) {
                wrapper.like(OperationLog::getOperator, operator);
            }
            if (status != null) {
                wrapper.eq(OperationLog::getStatus, status);
            }
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        List<OperationLog> list = operationLogMapper.selectList(wrapper);
        fillOperatorName(list);
        return list;
    }

    /**
     * 批量填充 operatorName：
     * - operator 为纯数字（历史 AOP 数据存的是 userId）：按 id 批量查询 sys_user 表获取 username
     * - operator 为非数字（init.sql 历史数据存的是 username，或 AOP 改造后存的真实 username）：直接当作 username
     */
    private void fillOperatorName(List<OperationLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        // 筛选出 operator 为纯数字的（即历史 AOP 写入的 userId）
        Set<Long> userIds = logs.stream()
                .map(OperationLog::getOperator)
                .filter(op -> op != null && op.matches("\\d+"))
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        // 批量查询用户表，建立 userId → username 映射
        Map<Long, String> userIdToName = Map.of();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
            userIdToName = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername, (a, b) -> a));
        }

        // 填充 operatorName
        for (OperationLog log : logs) {
            String op = log.getOperator();
            if (op == null || op.isEmpty()) {
                log.setOperatorName("系统");
            } else if (op.matches("\\d+")) {
                // 纯数字：是 userId，查表获取 username
                log.setOperatorName(userIdToName.getOrDefault(Long.valueOf(op), "用户" + op));
            } else {
                // 非数字：已经是 username，直接使用
                log.setOperatorName(op);
            }
        }
    }
}