package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Reconciliation;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import com.car.rental.module.statistics.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @GetMapping("/list")
    @RequirePermission("finance:reconciliation")
    public Result<PageResult<Reconciliation>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        IPage<Reconciliation> page = reconciliationService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("finance:reconciliation")
    public Result<Reconciliation> getById(@PathVariable Long id) {
        return Result.ok(reconciliationService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("finance:reconciliation")
    @LogChanges(
        entityClass = Reconciliation.class,
        mapperClass = ReconciliationMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "date:日期", "rentalIncome:租金收入",
            "fees:手续费", "netIncome:净收入",
            "status:状态", "checkedBy:对账人", "checkedAt:对账时间"
        }
    )
    public Result<Void> add(@RequestBody Reconciliation record) {
        reconciliationService.add(record);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    @RequirePermission("finance:reconciliation")
    @LogChanges(
        entityClass = Reconciliation.class,
        mapperClass = ReconciliationMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "date:日期", "rentalIncome:租金收入",
            "fees:手续费", "netIncome:净收入",
            "status:状态", "checkedBy:对账人", "checkedAt:对账时间"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody Reconciliation record) {
        reconciliationService.update(id, record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("finance:reconciliation")
    @LogChanges(
        entityClass = Reconciliation.class,
        mapperClass = ReconciliationMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "date:日期", "rentalIncome:租金收入",
            "fees:手续费", "netIncome:净收入",
            "status:状态", "checkedBy:对账人", "checkedAt:对账时间"
        }
    )
    public Result<Void> delete(@PathVariable Long id) {
        reconciliationService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @RequirePermission("finance:reconciliation")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = params.get("status") != null ? String.valueOf(params.get("status")) : null;
        reconciliationService.updateStatus(id, status);
        return Result.ok();
    }
}
