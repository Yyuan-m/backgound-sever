package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.statistics.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final FinanceRecordMapper financeRecordMapper;

    @Override
    public IPage<FinanceRecord> getRecords(Integer pageNum, Integer pageSize, String keyword, String type, String direction) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(FinanceRecord::getOrderNo, keyword)
                    .or()
                    .like(FinanceRecord::getCustomerName, keyword));
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        // 方向筛选：inflow=流入(rental)，outflow=流出(非rental)
        if ("inflow".equals(direction)) {
            wrapper.eq(FinanceRecord::getType, "rental");
        } else if ("outflow".equals(direction)) {
            wrapper.ne(FinanceRecord::getType, "rental");
        }
        wrapper.orderByDesc(FinanceRecord::getCreatedAt);

        Page<FinanceRecord> page = new Page<>(pageNum, pageSize);
        return financeRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public FinanceRecord getById(Long id) {
        FinanceRecord record = financeRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("财务记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void addRecord(FinanceRecord record) {
        record.setCreatedAt(LocalDateTime.now());
        financeRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void updateRecord(Long id, FinanceRecord record) {
        FinanceRecord existing = financeRecordMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("财务记录不存在");
        }
        existing.setType(record.getType());
        existing.setTypeName(record.getTypeName());
        existing.setOrderNo(record.getOrderNo());
        existing.setCustomerName(record.getCustomerName());
        existing.setAmount(record.getAmount());
        existing.setMethod(record.getMethod());
        existing.setStatus(record.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        financeRecordMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        FinanceRecord record = financeRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("财务记录不存在");
        }
        financeRecordMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getRevenueSummary(String period) {
        String dateFormat;
        switch (period) {
            case "year":
                dateFormat = "%Y";
                break;
            case "month":
                dateFormat = "%Y-%m";
                break;
            case "day":
            default:
                dateFormat = "%Y-%m-%d";
                break;
        }

        QueryWrapper<FinanceRecord> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(created_at, '" + dateFormat + "') as period",
                        "SUM(amount) as total_amount",
                        "COUNT(*) as record_count")
                .groupBy("DATE_FORMAT(created_at, '" + dateFormat + "')")
                .orderByAsc("period");
        return financeRecordMapper.selectMaps(wrapper);
    }
}