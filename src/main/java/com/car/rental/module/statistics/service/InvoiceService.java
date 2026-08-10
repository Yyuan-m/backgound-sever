package com.car.rental.module.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Invoice;

public interface InvoiceService {

    IPage<Invoice> getPageList(Integer pageNum, Integer pageSize, String keyword, String status);

    Invoice getById(Long id);

    void add(Invoice record);

    void update(Long id, Invoice record);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
