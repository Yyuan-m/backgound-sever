package com.car.rental.module.statistics.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.Invoice;

public interface InvoiceService {

    /** 分页查询 + summary（筛选结果发票金额合计，不受分页影响） */
    PageResult<Invoice> getPageList(Integer pageNum, Integer pageSize, String keyword, String status);

    Invoice getById(Long id);

    void add(Invoice record);

    void update(Long id, Invoice record);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
