package com.car.rental.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.Invoice;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import com.car.rental.module.statistics.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper invoiceMapper;

    @Override
    public PageResult<Invoice> getPageList(Integer pageNum, Integer pageSize, String keyword, String status) {
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Invoice::getOrderNo, keyword)
                    .or()
                    .like(Invoice::getCustomerName, keyword)
                    .or()
                    .like(Invoice::getInvoiceNo, keyword)
                    .or()
                    .like(Invoice::getTitle, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Invoice::getStatus, status);
        }
        wrapper.orderByDesc(Invoice::getCreatedAt);
        Page<Invoice> page = new Page<>(pageNum, pageSize);
        IPage<Invoice> result = invoiceMapper.selectPage(page, wrapper);

        // 筛选结果金额总计（不受分页影响）
        QueryWrapper<Invoice> sumWrapper = new QueryWrapper<>();
        sumWrapper.select("IFNULL(SUM(amount), 0) as amountTotal");
        if (StringUtils.hasText(keyword)) {
            sumWrapper.and(w -> w
                    .like("order_no", keyword)
                    .or()
                    .like("customer_name", keyword)
                    .or()
                    .like("invoice_no", keyword)
                    .or()
                    .like("title", keyword));
        }
        if (StringUtils.hasText(status)) {
            sumWrapper.eq("status", status);
        }
        List<Map<String, Object>> sumList = invoiceMapper.selectMaps(sumWrapper);
        Map<String, Object> summary = sumList.isEmpty() ? new HashMap<>() : sumList.get(0);
        return PageResult.of(result, summary);
    }

    @Override
    public Invoice getById(Long id) {
        Invoice record = invoiceMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("发票记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void add(Invoice record) {
        // 开具状态自动填充开票日期与发票号
        if ("issued".equals(record.getStatus())) {
            if (record.getIssueDate() == null) {
                record.setIssueDate(LocalDate.now());
            }
            if (!StringUtils.hasText(record.getInvoiceNo())) {
                record.setInvoiceNo("FP" + System.currentTimeMillis());
            }
        }
        record.setCreatedAt(LocalDateTime.now());
        invoiceMapper.insert(record);
    }

    @Override
    @Transactional
    public void update(Long id, Invoice record) {
        Invoice existing = invoiceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("发票记录不存在");
        }
        existing.setOrderNo(record.getOrderNo());
        existing.setCustomerName(record.getCustomerName());
        existing.setAmount(record.getAmount());
        existing.setType(record.getType());
        existing.setTitle(record.getTitle());
        existing.setTaxNo(record.getTaxNo());
        existing.setStatus(record.getStatus());
        existing.setIssueDate(record.getIssueDate());
        existing.setInvoiceNo(record.getInvoiceNo());
        existing.setUpdatedAt(LocalDateTime.now());
        invoiceMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Invoice record = invoiceMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("发票记录不存在");
        }
        invoiceMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        Invoice existing = invoiceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("发票记录不存在");
        }
        existing.setStatus(status);
        if ("issued".equals(status)) {
            if (existing.getIssueDate() == null) {
                existing.setIssueDate(LocalDate.now());
            }
            if (!StringUtils.hasText(existing.getInvoiceNo())) {
                existing.setInvoiceNo("FP" + System.currentTimeMillis());
            }
        }
        existing.setUpdatedAt(LocalDateTime.now());
        invoiceMapper.updateById(existing);
    }
}
