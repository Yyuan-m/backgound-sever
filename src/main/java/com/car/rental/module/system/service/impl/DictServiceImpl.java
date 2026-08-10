package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.SysDictData;
import com.car.rental.entity.SysDictType;
import com.car.rental.module.system.mapper.SysDictDataMapper;
import com.car.rental.module.system.mapper.SysDictTypeMapper;
import com.car.rental.module.system.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    // ===== DictType =====

    @Override
    public IPage<SysDictType> getTypePageList(long pageNum, long pageSize, String dictName, String dictType, Integer status) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictName)) {
            wrapper.like(SysDictType::getDictName, dictName);
        }
        if (StringUtils.hasText(dictType)) {
            wrapper.like(SysDictType::getDictType, dictType);
        }
        if (status != null) {
            wrapper.eq(SysDictType::getStatus, status);
        }
        wrapper.orderByDesc(SysDictType::getCreatedAt);
        return dictTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictType> getAllTypes() {
        return dictTypeMapper.selectList(null);
    }

    @Override
    public SysDictType getTypeById(Long id) {
        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        return dictType;
    }

    @Override
    public SysDictType getTypeByType(String dictType) {
        SysDictType type = dictTypeMapper.selectByType(dictType);
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        return type;
    }

    @Override
    @Transactional
    public void addDictType(SysDictType dictType) {
        dictType.setCreatedAt(LocalDateTime.now());
        dictTypeMapper.insert(dictType);
    }

    @Override
    @Transactional
    public void updateDictType(SysDictType dictType) {
        SysDictType existing = dictTypeMapper.selectById(dictType.getId());
        if (existing == null) {
            throw new BusinessException("字典类型不存在");
        }
        existing.setDictName(dictType.getDictName());
        existing.setDictType(dictType.getDictType());
        if (dictType.getStatus() != null) {
            existing.setStatus(dictType.getStatus());
        }
        existing.setRemark(dictType.getRemark());
        dictTypeMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteDictType(Long id) {
        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        long dataCount = dictDataMapper.selectCount(
                new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, dictType.getDictType()));
        if (dataCount > 0) {
            throw new BusinessException("该字典类型下存在 " + dataCount + " 条字典数据，请先删除字典数据后再删除类型");
        }
        dictTypeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteDictType(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            deleteDictType(id);
        }
    }

    @Override
    public long countDataByType(String dictType) {
        return dictDataMapper.selectCount(
                new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, dictType));
    }

    // ===== DictData =====

    @Override
    public List<SysDictData> getDataByType(String dictType) {
        return dictDataMapper.selectByType(dictType);
    }

    @Override
    public IPage<SysDictData> getDataPageList(long pageNum, long pageSize, String dictType, String dictLabel, Integer status) {
        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictType)) {
            wrapper.eq(SysDictData::getDictType, dictType);
        }
        if (StringUtils.hasText(dictLabel)) {
            wrapper.like(SysDictData::getDictLabel, dictLabel);
        }
        if (status != null) {
            wrapper.eq(SysDictData::getStatus, status);
        }
        wrapper.orderByAsc(SysDictData::getSortOrder);
        return dictDataMapper.selectPage(page, wrapper);
    }

    @Override
    public SysDictData getDataById(Long id) {
        SysDictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        return dictData;
    }

    @Override
    @Transactional
    public void addDictData(SysDictData dictData) {
        dictData.setCreatedAt(LocalDateTime.now());
        dictDataMapper.insert(dictData);
    }

    @Override
    @Transactional
    public void updateDictData(SysDictData dictData) {
        SysDictData existing = dictDataMapper.selectById(dictData.getId());
        if (existing == null) {
            throw new BusinessException("字典数据不存在");
        }
        existing.setDictType(dictData.getDictType());
        existing.setDictLabel(dictData.getDictLabel());
        existing.setDictValue(dictData.getDictValue());
        existing.setSortOrder(dictData.getSortOrder());
        if (dictData.getStatus() != null) {
            existing.setStatus(dictData.getStatus());
        }
        existing.setRemark(dictData.getRemark());
        dictDataMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteDictData(Long id) {
        SysDictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        dictDataMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteDictData(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        dictDataMapper.deleteBatchIds(ids);
    }
}