package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.CostRecord;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.service.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CostServiceImpl implements CostService {

    private final CostRecordMapper costRecordMapper;

    @Override
    public IPage<CostRecord> getPageList(Integer pageNum, Integer pageSize, String keyword, String type) {
        LambdaQueryWrapper<CostRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CostRecord::getDetail, keyword);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(CostRecord::getType, type);
        }
        wrapper.orderByDesc(CostRecord::getDate);
        Page<CostRecord> page = new Page<>(pageNum, pageSize);
        return costRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public CostRecord getById(Long id) {
        CostRecord record = costRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("成本记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void add(CostRecord record) {
        record.setCreatedAt(LocalDateTime.now());
        costRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void update(Long id, CostRecord record) {
        CostRecord existing = costRecordMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("成本记录不存在");
        }
        existing.setType(record.getType());
        existing.setTypeName(record.getTypeName());
        existing.setDetail(record.getDetail());
        existing.setAmount(record.getAmount());
        existing.setDate(record.getDate());
        existing.setUpdatedAt(LocalDateTime.now());
        costRecordMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CostRecord record = costRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("成本记录不存在");
        }
        costRecordMapper.deleteById(id);
    }
}
