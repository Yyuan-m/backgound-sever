package com.car.rental.module.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.FinanceRecord;

import java.util.List;
import java.util.Map;

public interface FinanceService {

    IPage<FinanceRecord> getRecords(Integer pageNum, Integer pageSize, String keyword, String type, String direction);

    FinanceRecord getById(Long id);

    void addRecord(FinanceRecord record);

    void updateRecord(Long id, FinanceRecord record);

    void deleteRecord(Long id);

    List<Map<String, Object>> getRevenueSummary(String period);
}