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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    // ===== DictType =====

    @GetMapping("/types")
    @RequirePermission("settings:dict")
    public Result<PageResult<SysDictType>> getTypes(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) Integer status) {
        IPage<SysDictType> page = dictService.getTypePageList(pageNum, pageSize, dictName, dictType, status);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/types/all")
    @RequirePermission("settings:dict")
    public Result<List<SysDictType>> allTypes() {
        return Result.ok(dictService.getAllTypes());
    }

    @GetMapping("/types/{type}")
    public Result<?> getTypeByType(@PathVariable String type) {
        return Result.ok(dictService.getTypeByType(type));
    }

    @PostMapping("/type")
    @RequirePermission("settings:dict")
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

    @PutMapping("/type")
    @RequirePermission("settings:dict")
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

    @DeleteMapping("/type/{id}")
    @RequirePermission("settings:dict")
    @LogChanges(
        entityClass = SysDictType.class,
        mapperClass = SysDictTypeMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "dictName:字典名称", "dictType:字典类型", "status:状态", "remark:备注"
        }
    )
    public Result<?> deleteType(@PathVariable Long id) {
        dictService.deleteDictType(id);
        return Result.ok();
    }

    @DeleteMapping("/type/batch")
    @RequirePermission("settings:dict")
    public Result<?> batchDeleteType(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        List<Long> ids = ((List<?>) idsObj) == null ? List.of()
                : ((List<?>) idsObj).stream().map(o -> Long.valueOf(o.toString())).toList();
        dictService.batchDeleteDictType(ids);
        return Result.ok();
    }

    // ===== DictData =====

    @GetMapping("/data")
    @RequirePermission("settings:dict")
    public Result<PageResult<SysDictData>> getDataPage(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) Integer status) {
        IPage<SysDictData> page = dictService.getDataPageList(pageNum, pageSize, dictType, dictLabel, status);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/data/{type}")
    public Result<?> getDataByType(@PathVariable String type) {
        return Result.ok(dictService.getDataByType(type));
    }

    @PostMapping("/data")
    @RequirePermission("settings:dict")
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

    @PutMapping("/data")
    @RequirePermission("settings:dict")
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

    @DeleteMapping("/data/{id}")
    @RequirePermission("settings:dict")
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
    public Result<?> deleteData(@PathVariable Long id) {
        dictService.deleteDictData(id);
        return Result.ok();
    }

    @DeleteMapping("/data/batch")
    @RequirePermission("settings:dict")
    public Result<?> batchDeleteData(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        List<Long> ids = ((List<?>) idsObj) == null ? List.of()
                : ((List<?>) idsObj).stream().map(o -> Long.valueOf(o.toString())).toList();
        dictService.batchDeleteDictData(ids);
        return Result.ok();
    }

    /** 删除字典类型前的数据校验：返回该 type 下的数据条数 */
    @GetMapping("/type/count-data/{type}")
    @RequirePermission("settings:dict")
    public Result<Map<String, Long>> countDataByType(@PathVariable String type) {
        return Result.ok(Map.of("count", dictService.countDataByType(type)));
    }
}