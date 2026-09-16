package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Reconciliation;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import com.car.rental.module.statistics.service.ReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "对账管理", description = "月度对账记录增删改查与对账状态流转；净收入 = 租金收入 - 手续费")
@RestController
@RequestMapping("/api/finance/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @Operation(summary = "对账记录列表（分页）", description = "按关键字、状态筛选月度对账记录。需要 finance:reconciliation 权限")
    @GetMapping("/list")
    @RequirePermission("finance:reconciliation")
    public Result<PageResult<Reconciliation>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "对账状态（pending 待对账 / checked 已对账）") @RequestParam(required = false) String status) {
        IPage<Reconciliation> page = reconciliationService.getPageList(pageNum, pageSize, keyword, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "对账记录详情", description = "按 ID 查询单条对账记录，不存在时抛业务异常。需要 finance:reconciliation 权限")
    @GetMapping("/{id}")
    @RequirePermission("finance:reconciliation")
    public Result<Reconciliation> getById(@Parameter(description = "对账记录ID") @PathVariable Long id) {
        return Result.ok(reconciliationService.getById(id));
    }

    @Operation(summary = "新增对账记录", description = "手工录入月度对账记录；净收入自动计算 = 租金收入 - 手续费。需要 finance:reconciliation:add 权限")
    @PostMapping("/add")
    @RequirePermission("finance:reconciliation:add")
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

    @Operation(summary = "编辑对账记录", description = "按 ID 更新对账记录（日期/租金收入/手续费/状态/对账人等）；净收入自动重算 = 租金收入 - 手续费。需要 finance:reconciliation:update 权限")
    @PutMapping("/update/{id}")
    @RequirePermission("finance:reconciliation:update")
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
    public Result<Void> update(@Parameter(description = "对账记录ID") @PathVariable Long id, @RequestBody Reconciliation record) {
        reconciliationService.update(id, record);
        return Result.ok();
    }

    @Operation(summary = "删除对账记录", description = "按 ID 逻辑删除对账记录。需要 finance:reconciliation:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("finance:reconciliation:delete")
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
    public Result<Void> delete(@Parameter(description = "对账记录ID") @PathVariable Long id) {
        reconciliationService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "更新对账状态", description = "修改对账状态（pending 待对账 → checked 已对账）；置为 checked 时自动填充对账人与对账时间，请求体为 {\"status\": \"checked\"}。需要 finance:reconciliation:status 权限")
    @PutMapping("/{id}/status")
    @RequirePermission("finance:reconciliation:status")
    public Result<Void> updateStatus(@Parameter(description = "对账记录ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = params.get("status") != null ? String.valueOf(params.get("status")) : null;
        reconciliationService.updateStatus(id, status);
        return Result.ok();
    }
}
