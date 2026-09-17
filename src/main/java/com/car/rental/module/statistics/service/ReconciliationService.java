package com.car.rental.module.statistics.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.Reconciliation;

public interface ReconciliationService {

    /** 分页查询 + summary（筛选结果租金/费用/净收入合计，不受分页影响） */
    PageResult<Reconciliation> getPageList(Integer pageNum, Integer pageSize, String keyword, String status);

    Reconciliation getById(Long id);

    void add(Reconciliation record);

    void update(Long id, Reconciliation record);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
