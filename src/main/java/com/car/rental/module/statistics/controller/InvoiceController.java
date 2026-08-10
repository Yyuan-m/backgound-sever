package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Invoice;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import com.car.rental.module.statistics.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/list")
    @RequirePermission("finance:invoice")
    public Result<PageResult<Invoice>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        IPage<Invoice> page = invoiceService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("finance:invoice")
    public Result<Invoice> getById(@PathVariable Long id) {
        return Result.ok(invoiceService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("finance:invoice")
    @LogChanges(
        entityClass = Invoice.class,
        mapperClass = InvoiceMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "customerName:客户姓名", "amount:金额",
            "type:类型", "title:抬头", "taxNo:税号",
            "status:状态", "issueDate:开票日期", "invoiceNo:发票号"
        }
    )
    public Result<Void> add(@RequestBody Invoice record) {
        invoiceService.add(record);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    @RequirePermission("finance:invoice")
    @LogChanges(
        entityClass = Invoice.class,
        mapperClass = InvoiceMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "customerName:客户姓名", "amount:金额",
            "type:类型", "title:抬头", "taxNo:税号",
            "status:状态", "issueDate:开票日期", "invoiceNo:发票号"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody Invoice record) {
        invoiceService.update(id, record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("finance:invoice")
    @LogChanges(
        entityClass = Invoice.class,
        mapperClass = InvoiceMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "customerName:客户姓名", "amount:金额",
            "type:类型", "title:抬头", "taxNo:税号",
            "status:状态", "issueDate:开票日期", "invoiceNo:发票号"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @RequirePermission("finance:invoice")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = params.get("status") != null ? String.valueOf(params.get("status")) : null;
        invoiceService.updateStatus(id, status);
        return Result.ok();
    }
}
