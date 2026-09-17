package com.car.rental.module.statistics.controller;

import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CostRecord;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.service.CostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "成本管理", description = "成本记录增删改查（车辆租赁成本、维保成本、手工录入成本等）")
@RestController
@RequestMapping("/api/finance/cost")
@RequiredArgsConstructor
public class CostController {

    private final CostService costService;

    @Operation(summary = "成本记录列表（分页）", description = "按关键字、成本类型筛选成本记录。需要 finance:cost 权限")
    @GetMapping("/list")
    @RequirePermission("finance:cost")
    public Result<PageResult<CostRecord>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：明细模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "成本类型") @RequestParam(required = false) String type) {
        return Result.ok(costService.getPageList(pageNum, pageSize, keyword, type));
    }

    @Operation(summary = "成本记录详情", description = "按 ID 查询单条成本记录。需要 finance:cost 权限")
    @GetMapping("/{id}")
    @RequirePermission("finance:cost")
    public Result<CostRecord> getById(@Parameter(description = "成本记录ID") @PathVariable Long id) {
        return Result.ok(costService.getById(id));
    }

    @Operation(summary = "新增成本记录", description = "手工录入成本记录（类型/明细/金额/日期）。需要 finance:cost:add 权限")
    @PostMapping("/add")
    @RequirePermission("finance:cost:add")
    @LogChanges(
        entityClass = CostRecord.class,
        mapperClass = CostRecordMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "detail:明细",
            "amount:金额", "date:日期"
        }
    )
    public Result<Void> add(@RequestBody CostRecord record) {
        costService.add(record);
        return Result.ok();
    }

    @Operation(summary = "编辑成本记录", description = "按 ID 更新成本记录（类型/明细/金额/日期）。需要 finance:cost:update 权限")
    @PutMapping("/update/{id}")
    @RequirePermission("finance:cost:update")
    @LogChanges(
        entityClass = CostRecord.class,
        mapperClass = CostRecordMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "detail:明细",
            "amount:金额", "date:日期"
        }
    )
    public Result<Void> update(@Parameter(description = "成本记录ID") @PathVariable Long id, @RequestBody CostRecord record) {
        costService.update(id, record);
        return Result.ok();
    }

    @Operation(summary = "删除成本记录", description = "按 ID 逻辑删除成本记录。需要 finance:cost:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("finance:cost:delete")
    @LogChanges(
        entityClass = CostRecord.class,
        mapperClass = CostRecordMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "detail:明细",
            "amount:金额", "date:日期"
        }
    )
    public Result<Void> delete(@Parameter(description = "成本记录ID") @PathVariable Long id) {
        costService.delete(id);
        return Result.ok();
    }
}
