package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysDictData;
import com.car.rental.entity.SysDictType;
import com.car.rental.module.system.mapper.SysDictDataMapper;
import com.car.rental.module.system.mapper.SysDictTypeMapper;
import com.car.rental.module.system.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "字典管理", description = "数据字典类型与字典数据的维护，并提供按类型标识取值的通用查询")
@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    // ===== DictType =====

    @Operation(summary = "字典类型列表（分页）", description = "说明：分页查询字典类型，支持名称、类型标识与状态筛选。需要 settings:dict 权限")
    @GetMapping("/types")
    @RequirePermission("settings:dict")
    public Result<PageResult<SysDictType>> getTypes(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "字典名称模糊匹配") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典类型标识") @RequestParam(required = false) String dictType,
            @Parameter(description = "状态筛选（1 启用 / 0 禁用）") @RequestParam(required = false) Integer status) {
        IPage<SysDictType> page = dictService.getTypePageList(pageNum, pageSize, dictName, dictType, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "全部字典类型", description = "说明：不分页返回全部字典类型，用于下拉选择。需要 settings:dict 权限")
    @GetMapping("/types/all")
    @RequirePermission("settings:dict")
    public Result<List<SysDictType>> allTypes() {
        return Result.ok(dictService.getAllTypes());
    }

    @Operation(summary = "按类型标识查字典类型", description = "说明：按唯一类型标识查询字典类型，不存在时报错；无需专门权限，供业务模块调用")
    @GetMapping("/types/{type}")
    public Result<?> getTypeByType(@Parameter(description = "字典类型标识") @PathVariable String type) {
        return Result.ok(dictService.getTypeByType(type));
    }

    @Operation(summary = "新增字典类型", description = "说明：新建字典类型（名称/类型标识/状态/备注）。需要 settings:dict:add 权限")
    @PostMapping("/type")
    @RequirePermission("settings:dict:add")
    @LogChanges(
        entityClass = SysDictType.class,
        mapperClass = SysDictTypeMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictName:字典名称", "dictType:字典类型", "status:状态", "remark:备注"
        }
    )
    public Result<?> addType(@RequestBody SysDictType dictType) {
        dictService.addDictType(dictType);
        return Result.ok();
    }

    @Operation(summary = "编辑字典类型", description = "说明：修改字典类型的名称、状态与备注。需要 settings:dict:update 权限")
    @PutMapping("/type")
    @RequirePermission("settings:dict:update")
    @LogChanges(
        entityClass = SysDictType.class,
        mapperClass = SysDictTypeMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictName:字典名称", "dictType:字典类型", "status:状态", "remark:备注"
        }
    )
    public Result<?> updateType(@RequestBody SysDictType dictType) {
        dictService.updateDictType(dictType);
        return Result.ok();
    }

    @Operation(summary = "删除字典类型", description = "说明：该类型下存在字典数据时禁止删除，需先删除全部字典数据。需要 settings:dict:delete 权限")
    @DeleteMapping("/type/{id}")
    @RequirePermission("settings:dict:delete")
    @LogChanges(
        entityClass = SysDictType.class,
        mapperClass = SysDictTypeMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictName:字典名称", "dictType:字典类型", "status:状态", "remark:备注"
        }
    )
    public Result<?> deleteType(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        dictService.deleteDictType(id);
        return Result.ok();
    }

    @Operation(summary = "批量删除字典类型", description = "说明：body 传 ids 类型ID数组；类型下存在字典数据时同样禁止删除。需要 settings:dict:delete 权限")
    @DeleteMapping("/type/batch")
    @RequirePermission("settings:dict:delete")
    public Result<?> batchDeleteType(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        List<Long> ids = ((List<?>) idsObj) == null ? List.of()
                : ((List<?>) idsObj).stream().map(o -> Long.valueOf(o.toString())).toList();
        dictService.batchDeleteDictType(ids);
        return Result.ok();
    }

    // ===== DictData =====

    @Operation(summary = "字典数据列表（分页）", description = "说明：分页查询字典数据，支持字典类型、标签与状态筛选。需要 settings:dict 权限")
    @GetMapping("/data")
    @RequirePermission("settings:dict")
    public Result<PageResult<SysDictData>> getDataPage(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "字典类型标识") @RequestParam(required = false) String dictType,
            @Parameter(description = "字典标签模糊匹配") @RequestParam(required = false) String dictLabel,
            @Parameter(description = "状态筛选（1 启用 / 0 禁用）") @RequestParam(required = false) Integer status) {
        IPage<SysDictData> page = dictService.getDataPageList(pageNum, pageSize, dictType, dictLabel, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "按类型取字典数据", description = "说明：返回指定类型标识下的全部字典数据（含标签/值/排序），供下拉选项等业务场景使用，无需专门权限")
    @GetMapping("/data/{type}")
    public Result<?> getDataByType(@Parameter(description = "字典类型标识") @PathVariable String type) {
        return Result.ok(dictService.getDataByType(type));
    }

    @Operation(summary = "新增字典数据", description = "说明：新建字典数据项（类型/标签/值/排序/状态/备注）。需要 settings:dict:add 权限")
    @PostMapping("/data")
    @RequirePermission("settings:dict:add")
    @LogChanges(
        entityClass = SysDictData.class,
        mapperClass = SysDictDataMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictType:字典类型", "dictLabel:字典标签", "dictValue:字典值",
            "sortOrder:排序", "status:状态", "remark:备注"
        }
    )
    public Result<?> addData(@RequestBody SysDictData dictData) {
        dictService.addDictData(dictData);
        return Result.ok();
    }

    @Operation(summary = "编辑字典数据", description = "说明：修改字典数据项的标签、值、排序与状态。需要 settings:dict:update 权限")
    @PutMapping("/data")
    @RequirePermission("settings:dict:update")
    @LogChanges(
        entityClass = SysDictData.class,
        mapperClass = SysDictDataMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictType:字典类型", "dictLabel:字典标签", "dictValue:字典值",
            "sortOrder:排序", "status:状态", "remark:备注"
        }
    )
    public Result<?> updateData(@RequestBody SysDictData dictData) {
        dictService.updateDictData(dictData);
        return Result.ok();
    }

    @Operation(summary = "删除字典数据", description = "说明：按 ID 删除字典数据项。需要 settings:dict:delete 权限")
    @DeleteMapping("/data/{id}")
    @RequirePermission("settings:dict:delete")
    @LogChanges(
        entityClass = SysDictData.class,
        mapperClass = SysDictDataMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictType:字典类型", "dictLabel:字典标签", "dictValue:字典值",
            "sortOrder:排序", "status:状态", "remark:备注"
        }
    )
    public Result<?> deleteData(@Parameter(description = "字典数据ID") @PathVariable Long id) {
        dictService.deleteDictData(id);
        return Result.ok();
    }

    @Operation(summary = "批量删除字典数据", description = "说明：body 传 ids 数据ID数组批量删除。需要 settings:dict:delete 权限")
    @DeleteMapping("/data/batch")
    @RequirePermission("settings:dict:delete")
    public Result<?> batchDeleteData(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        List<Long> ids = ((List<?>) idsObj) == null ? List.of()
                : ((List<?>) idsObj).stream().map(o -> Long.valueOf(o.toString())).toList();
        dictService.batchDeleteDictData(ids);
        return Result.ok();
    }

    /** 删除字典类型前的数据校验：返回该 type 下的数据条数 */
    @Operation(summary = "统计类型下数据条数", description = "说明：删除字典类型前的校验，返回 {count: 该类型下的字典数据条数}。需要 settings:dict 权限")
    @GetMapping("/type/count-data/{type}")
    @RequirePermission("settings:dict")
    public Result<Map<String, Long>> countDataByType(@Parameter(description = "字典类型标识") @PathVariable String type) {
        return Result.ok(Map.of("count", dictService.countDataByType(type)));
    }
}
