package com.car.rental.module.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CostRecord;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.service.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/cost")
@RequiredArgsConstructor
public class CostController {

    private final CostService costService;

    @GetMapping("/list")
    @RequirePermission("finance:cost")
    public Result<PageResult<CostRecord>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        IPage<CostRecord> page = costService.getPageList(pageNum, pageSize, keyword, type);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("finance:cost")
    public Result<CostRecord> getById(@PathVariable Long id) {
        return Result.ok(costService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("finance:cost")
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

    @PutMapping("/update/{id}")
    @RequirePermission("finance:cost")
    @LogChanges(
        entityClass = CostRecord.class,
        mapperClass = CostRecordMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "type:类型", "typeName:类型名称", "detail:明细",
            "amount:金额", "date:日期"
        }
    )
    public Result<Void> update(@PathVariable Long id, @RequestBody CostRecord record) {
        costService.update(id, record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("finance:cost")
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
    public Result<Void> delete(@PathVariable Long id) {
        costService.delete(id);
        return Result.ok();
    }
}
