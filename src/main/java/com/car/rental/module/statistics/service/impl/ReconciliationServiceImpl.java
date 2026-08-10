package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.Reconciliation;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import com.car.rental.module.statistics.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService {

    private final ReconciliationMapper reconciliationMapper;

    @Override
    public IPage<Reconciliation> getPageList(Integer pageNum, Integer pageSize, String keyword, String status) {
        LambdaQueryWrapper<Reconciliation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Reconciliation::getCheckedBy, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Reconciliation::getStatus, status);
        }
        wrapper.orderByDesc(Reconciliation::getDate);
        Page<Reconciliation> page = new Page<>(pageNum, pageSize);
        return reconciliationMapper.selectPage(page, wrapper);
    }

    @Override
    public Reconciliation getById(Long id) {
        Reconciliation record = reconciliationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("对账记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void add(Reconciliation record) {
        // 自动计算净收入
        record.setNetIncome(calcNetIncome(record));
        record.setCreatedAt(LocalDateTime.now());
        reconciliationMapper.insert(record);
    }

    @Override
    @Transactional
    public void update(Long id, Reconciliation record) {
        Reconciliation existing = reconciliationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("对账记录不存在");
        }
        existing.setDate(record.getDate());
        existing.setRentalIncome(record.getRentalIncome());
        existing.setFees(record.getFees());
        existing.setNetIncome(calcNetIncome(record));
        existing.setStatus(record.getStatus());
        existing.setCheckedBy(record.getCheckedBy());
        existing.setCheckedAt(record.getCheckedAt());
        existing.setUpdatedAt(LocalDateTime.now());
        reconciliationMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reconciliation record = reconciliationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("对账记录不存在");
        }
        reconciliationMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        Reconciliation existing = reconciliationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("对账记录不存在");
        }
        existing.setStatus(status);
        if ("checked".equals(status)) {
            if (!StringUtils.hasText(existing.getCheckedBy())) {
                existing.setCheckedBy("当前用户");
            }
            if (existing.getCheckedAt() == null) {
                existing.setCheckedAt(LocalDateTime.now());
            }
        }
        existing.setUpdatedAt(LocalDateTime.now());
        reconciliationMapper.updateById(existing);
    }

    /** 净收入 = 租金收入 - 其他费用 */
    private java.math.BigDecimal calcNetIncome(Reconciliation r) {
        java.math.BigDecimal rental = r.getRentalIncome() == null ? java.math.BigDecimal.ZERO : r.getRentalIncome();
        java.math.BigDecimal fees = r.getFees() == null ? java.math.BigDecimal.ZERO : r.getFees();
        return rental.subtract(fees);
    }
}
