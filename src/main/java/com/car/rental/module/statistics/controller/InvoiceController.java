package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Invoice;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import com.car.rental.module.statistics.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "发票管理", description = "发票增删改查与开票状态流转；订单完成时自动生成待开票发票（金额=租金总额）")
@RestController
@RequestMapping("/api/finance/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "发票列表（分页）", description = "按关键字、状态筛选发票，按创建时间倒序。需要 finance:invoice 权限")
    @GetMapping("/list")
    @RequirePermission("finance:invoice")
    public Result<PageResult<Invoice>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：订单号/客户姓名/发票号/抬头模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "发票状态（pending 待开票 / issued 已开具）") @RequestParam(required = false) String status) {
        IPage<Invoice> page = invoiceService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "发票详情", description = "按 ID 查询单张发票，不存在时抛业务异常。需要 finance:invoice 权限")
    @GetMapping("/{id}")
    @RequirePermission("finance:invoice")
    public Result<Invoice> getById(@Parameter(description = "发票ID") @PathVariable Long id) {
        return Result.ok(invoiceService.getById(id));
    }

    @Operation(summary = "新增发票", description = "手工录入发票；状态为 issued（已开具）时自动填充开票日期与发票号（未传时生成 FP+时间戳）。需要 finance:invoice:add 权限")
    @PostMapping("/add")
    @RequirePermission("finance:invoice:add")
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

    @Operation(summary = "编辑发票", description = "按 ID 更新发票（订单号/客户/金额/类型/抬头/税号/状态/开票日期/发票号）。需要 finance:invoice:update 权限")
    @PutMapping("/update/{id}")
    @RequirePermission("finance:invoice:update")
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
    public Result<Void> update(@Parameter(description = "发票ID") @PathVariable Long id, @RequestBody Invoice record) {
        invoiceService.update(id, record);
        return Result.ok();
    }

    @Operation(summary = "删除发票", description = "按 ID 逻辑删除发票。需要 finance:invoice:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("finance:invoice:delete")
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
    public Result<Void> delete(@Parameter(description = "发票ID") @PathVariable Long id) {
        invoiceService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "更新发票状态", description = "修改开票状态（pending 待开票 → issued 已开具）；置为 issued 时自动填充开票日期与发票号，请求体为 {\"status\": \"issued\"}。需要 finance:invoice:status 权限")
    @PutMapping("/{id}/status")
    @RequirePermission("finance:invoice:status")
    public Result<Void> updateStatus(@Parameter(description = "发票ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = params.get("status") != null ? String.valueOf(params.get("status")) : null;
        invoiceService.updateStatus(id, status);
        return Result.ok();
    }
}
