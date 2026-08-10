package com.car.rental.module.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.CostRecord;

public interface CostService {

    IPage<CostRecord> getPageList(Integer pageNum, Integer pageSize, String keyword, String type);

    CostRecord getById(Long id);

    void add(CostRecord record);

    void update(Long id, CostRecord record);

    void delete(Long id);
}
