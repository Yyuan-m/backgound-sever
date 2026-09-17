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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final FinanceRecordMapper financeRecordMapper;

    @Override
    public PageResult<FinanceRecord> getRecords(Integer pageNum, Integer pageSize, String keyword, String type, String direction) {
        Page<FinanceRecord> page = new Page<>(pageNum, pageSize);
        IPage<FinanceRecord> result = financeRecordMapper.selectPage(page, buildRecordWrapper(keyword, type, direction));

        // 筛选结果金额总计（不受分页影响，用于前端合计行）
        QueryWrapper<FinanceRecord> sumWrapper = new QueryWrapper<>();
        sumWrapper.select("IFNULL(SUM(CASE WHEN type = 'rental' THEN amount ELSE 0 END), 0) as inflowTotal",
                        "IFNULL(SUM(CASE WHEN type <> 'rental' THEN amount ELSE 0 END), 0) as outflowTotal");
        applyRecordFilters(sumWrapper, keyword, type, direction);
        List<Map<String, Object>> sumList = financeRecordMapper.selectMaps(sumWrapper);
        Map<String, Object> summary = sumList.isEmpty() ? new HashMap<>() : sumList.get(0);
        BigDecimal inflow = toBigDecimal(summary.get("inflowTotal"));
        BigDecimal outflow = toBigDecimal(summary.get("outflowTotal"));
        summary.put("inflowTotal", inflow);
        summary.put("outflowTotal", outflow);
        summary.put("netTotal", inflow.subtract(outflow));
        return PageResult.of(result, summary);
    }

    private LambdaQueryWrapper<FinanceRecord> buildRecordWrapper(String keyword, String type, String direction) {
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
        return wrapper;
    }

    /** 对 QueryWrapper 应用与分页查询一致的筛选条件（用于合计查询） */
    private void applyRecordFilters(QueryWrapper<FinanceRecord> wrapper, String keyword, String type, String direction) {
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like("order_no", keyword)
                    .or()
                    .like("customer_name", keyword));
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if ("inflow".equals(direction)) {
            wrapper.eq("type", "rental");
        } else if ("outflow".equals(direction)) {
            wrapper.ne("type", "rental");
        }
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        return new BigDecimal(obj.toString());
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