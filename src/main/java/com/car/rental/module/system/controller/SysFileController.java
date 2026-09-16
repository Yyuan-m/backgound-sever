package com.car.rental.module.system.controller;

import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.SysFile;
import com.car.rental.module.system.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "文件管理", description = "上传文件的分页查询、回收站（逻辑删除/恢复）与物理删除")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService sysFileService;

    /**
     * 分页查询文件列表
     * 支持筛选：originalName、category、bizType、uploadedBy、status
     */
    @Operation(summary = "文件列表（分页）", description = "说明：分页查询文件元信息，支持按原文件名、分类、业务类型、上传人、状态筛选。需要 system:file 权限")
    @GetMapping("/list")
    @RequirePermission("system:file")
    public Result<PageResult<SysFile>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "原文件名模糊匹配") @RequestParam(required = false) String originalName,
            @Parameter(description = "文件分类") @RequestParam(required = false) String category,
            @Parameter(description = "业务类型") @RequestParam(required = false) String bizType,
            @Parameter(description = "上传人用户ID") @RequestParam(required = false) Long uploadedBy,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        return Result.ok(sysFileService.list(pageNum, pageSize,
                originalName, category, bizType, uploadedBy, status));
    }

    /** 文件详情 */
    @Operation(summary = "文件详情", description = "说明：按 ID 查询文件元信息。需要 system:file 权限")
    @GetMapping("/{id}")
    @RequirePermission("system:file")
    public Result<SysFile> getById(@Parameter(description = "文件ID") @PathVariable Long id) {
        return Result.ok(sysFileService.getById(id));
    }

    /** 逻辑删除（标记为已删除，移入回收站） */
    @Operation(summary = "删除文件（移入回收站）", description = "说明：逻辑删除，仅标记为已删除并移入回收站，磁盘文件保留。需要 system:file:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("system:file:delete")
    public Result<Void> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        sysFileService.delete(id);
        return Result.ok();
    }

    /** 批量逻辑删除（移入回收站） */
    @Operation(summary = "批量删除文件（移入回收站）", description = "说明：body 传 ids 文件ID数组，批量逻辑删除移入回收站。需要 system:file:delete 权限")
    @PostMapping("/batch")
    @RequirePermission("system:file:delete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        sysFileService.batchDelete(ids);
        return Result.ok();
    }

    /** 物理删除（同时删除磁盘文件，谨慎使用） */
    @Operation(summary = "物理删除文件", description = "说明：同时删除磁盘上的真实文件，不可恢复，谨慎使用。需要 system:file:delete 权限")
    @DeleteMapping("/physical/{id}")
    @RequirePermission("system:file:delete")
    public Result<Void> physicalDelete(@Parameter(description = "文件ID") @PathVariable Long id) {
        sysFileService.physicalDelete(id);
        return Result.ok();
    }

    /** 批量物理删除（同时删除磁盘文件） */
    @Operation(summary = "批量物理删除文件", description = "说明：body 传 ids 文件ID数组，同时删除磁盘文件，不可恢复。需要 system:file:delete 权限")
    @PostMapping("/batch-physical")
    @RequirePermission("system:file:delete")
    public Result<Void> batchPhysicalDelete(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        sysFileService.batchPhysicalDelete(ids);
        return Result.ok();
    }

    /** 恢复文件（从回收站恢复为正常状态） */
    @Operation(summary = "恢复文件", description = "说明：从回收站恢复为正常状态。需要 system:file:restore 权限")
    @PostMapping("/restore/{id}")
    @RequirePermission("system:file:restore")
    public Result<Void> restore(@Parameter(description = "文件ID") @PathVariable Long id) {
        sysFileService.restore(id);
        return Result.ok();
    }

    /** 批量恢复文件 */
    @Operation(summary = "批量恢复文件", description = "说明：body 传 ids 文件ID数组，从回收站批量恢复。需要 system:file:restore 权限")
    @PostMapping("/batch-restore")
    @RequirePermission("system:file:restore")
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
