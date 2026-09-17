package com.car.rental.module.statistics.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CostRecord;

public interface CostService {

    /** 分页查询 + summary（筛选结果成本金额合计，不受分页影响） */
    PageResult<CostRecord> getPageList(Integer pageNum, Integer pageSize, String keyword, String type);

    CostRecord getById(Long id);

    void add(CostRecord record);

    void update(Long id, CostRecord record);

    void delete(Long id);
}
