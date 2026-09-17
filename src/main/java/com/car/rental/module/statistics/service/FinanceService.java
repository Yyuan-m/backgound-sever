package com.car.rental.module.statistics.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.FinanceRecord;

import java.util.List;
import java.util.Map;

public interface FinanceService {

    /** 分页查询 + summary（筛选结果流入/流出/净额合计，不受分页影响） */
    PageResult<FinanceRecord> getRecords(Integer pageNum, Integer pageSize, String keyword, String type, String direction);

    FinanceRecord getById(Long id);

    void addRecord(FinanceRecord record);

    void updateRecord(Long id, FinanceRecord record);

    void deleteRecord(Long id);

    List<Map<String, Object>> getRevenueSummary(String period);
}