package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysFile;
import com.car.rental.module.system.service.SysFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService sysFileService;

    /**
     * 分页查询文件列表
     * 支持筛选：originalName、category、bizType、uploadedBy、status
     */
    @GetMapping("/list")
    @RequirePermission("system:file")
    public Result<PageResult<SysFile>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String originalName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long uploadedBy,
            @RequestParam(required = false) Integer status) {
        return Result.ok(sysFileService.list(pageNum, pageSize,
                originalName, category, bizType, uploadedBy, status));
    }

    /** 文件详情 */
    @GetMapping("/{id}")
    @RequirePermission("system:file")
    public Result<SysFile> getById(@PathVariable Long id) {
        return Result.ok(sysFileService.getById(id));
    }

    /** 逻辑删除（标记为已删除，移入回收站） */
    @DeleteMapping("/{id}")
    @RequirePermission("system:file")
    public Result<Void> delete(@PathVariable Long id) {
        sysFileService.delete(id);
        return Result.ok();
    }

    /** 批量逻辑删除（移入回收站） */
    @PostMapping("/batch")
    @RequirePermission("system:file")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        sysFileService.batchDelete(ids);
        return Result.ok();
    }

    /** 物理删除（同时删除磁盘文件，谨慎使用） */
    @DeleteMapping("/physical/{id}")
    @RequirePermission("system:file")
    public Result<Void> physicalDelete(@PathVariable Long id) {
        sysFileService.physicalDelete(id);
        return Result.ok();
    }

    /** 批量物理删除（同时删除磁盘文件） */
    @PostMapping("/batch-physical")
    @RequirePermission("system:file")
    public Result<Void> batchPhysicalDelete(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        sysFileService.batchPhysicalDelete(ids);
        return Result.ok();
    }

    /** 恢复文件（从回收站恢复为正常状态） */
    @PostMapping("/restore/{id}")
    @RequirePermission("system:file")
    public Result<Void> restore(@PathVariable Long id) {
        sysFileService.restore(id);
        return Result.ok();
    }

    /** 批量恢复文件 */
    @PostMapping("/batch-restore")
    @RequirePermission("system:file")
    public Result<Void> batchRestore(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        sysFileService.batchRestore(ids);
        return Result.ok();
    }

    /** 解析请求体中的 ids 列表（兼容 Integer/Long/String） */
    @SuppressWarnings("unchecked")
    private List<Long> parseIds(Object idsObj) {
        if (idsObj == null) return List.of();
        return ((List<?>) idsObj).stream().map(o -> Long.valueOf(o.toString())).toList();
    }
}
