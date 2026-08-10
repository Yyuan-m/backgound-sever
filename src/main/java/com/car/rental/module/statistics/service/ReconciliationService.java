package com.car.rental.module.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Reconciliation;

public interface ReconciliationService {

    IPage<Reconciliation> getPageList(Integer pageNum, Integer pageSize, String keyword, String status);

    Reconciliation getById(Long id);

    void add(Reconciliation record);

    void update(Long id, Reconciliation record);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
