package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.SysDictData;
import com.car.rental.entity.SysDictType;

import java.util.List;

public interface DictService {

    // DictType
    IPage<SysDictType> getTypePageList(long pageNum, long pageSize, String dictName, String dictType, Integer status);

    List<SysDictType> getAllTypes();

    SysDictType getTypeById(Long id);

    SysDictType getTypeByType(String dictType);

    void addDictType(SysDictType dictType);

    void updateDictType(SysDictType dictType);

    void deleteDictType(Long id);

    void batchDeleteDictType(List<Long> ids);

    /** 统计某 type 编码下的字典数据条数 */
    long countDataByType(String dictType);

    // DictData
    List<SysDictData> getDataByType(String dictType);

    IPage<SysDictData> getDataPageList(long pageNum, long pageSize, String dictType, String dictLabel, Integer status);

    SysDictData getDataById(Long id);

    void addDictData(SysDictData dictData);

    void updateDictData(SysDictData dictData);

    void deleteDictData(Long id);

    void batchDeleteDictData(List<Long> ids);
}