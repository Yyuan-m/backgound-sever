package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.OperationLog;
import com.car.rental.module.system.service.OperationLogService;
import com.car.rental.module.system.util.OperationLogExporter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "操作日志", description = "系统操作日志的分页查询、详情查看与 Excel/PDF/Markdown 导出")
@RestController
@RequestMapping("/api/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;
    private final OperationLogExporter operationLogExporter;

    @Operation(summary = "操作日志列表（分页）", description = "说明：分页查询操作日志，支持模块、操作、操作人、状态筛选。需要 settings:system 权限")
    @GetMapping("/list")
    @RequirePermission("settings:system")
    public Result<PageResult<OperationLog>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "模块名筛选") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型筛选") @RequestParam(required = false) String action,
            @Parameter(description = "操作人筛选") @RequestParam(required = false) String operator,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        IPage<OperationLog> page = operationLogService.getList(pageNum, pageSize, module, action, operator, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "操作日志详情", description = "说明：按 ID 查询单条操作日志明细。需要 settings:system 权限")
    @GetMapping("/{id}")
    @RequirePermission("settings:system")
    public Result<OperationLog> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        return Result.ok(operationLogService.getById(id));
    }

    /**
     * 导出操作日志：
     * - format: excel / pdf / markdown（不区分大小写）
     * - ids：逗号分隔的日志 ID 列表，非空时仅导出选中的日志（忽略其他筛选条件）
     * - module / action / operator / status：筛选条件，仅在 ids 为空时生效，导出符合条件的全部日志
     */
    @Operation(summary = "导出操作日志", description = "说明：支持 excel / pdf / markdown 三种格式（不区分大小写）；ids 传逗号分隔的日志ID时仅导出选中项并忽略筛选条件，否则按筛选条件导出全部；以文件流下载，文件名形如 操作日志_20260731_153000.xlsx。需要 settings:system 权限")
    @GetMapping("/export")
    @RequirePermission("settings:system")
    public void export(
            @Parameter(description = "导出格式：excel / pdf / markdown，默认 excel") @RequestParam(defaultValue = "excel") String format,
            @Parameter(description = "选中导出的日志ID列表，逗号分隔，非空时忽略筛选条件") @RequestParam(required = false) String ids,
            @Parameter(description = "模块名筛选（ids 为空时生效）") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型筛选（ids 为空时生效）") @RequestParam(required = false) String action,
            @Parameter(description = "操作人筛选（ids 为空时生效）") @RequestParam(required = false) String operator,
            @Parameter(description = "状态筛选（ids 为空时生效）") @RequestParam(required = false) Integer status,
            HttpServletResponse response) {
        OperationLogExporter.Format fmt = OperationLogExporter.parseFormat(format);
        List<Long> idList = parseIds(ids);
        List<OperationLog> logs = operationLogService.getForExport(idList, module, action, operator, status);

        String fileName = buildFileName(fmt);
        setResponseHeaders(response, fmt, fileName);

        try (OutputStream out = response.getOutputStream()) {
            operationLogExporter.write(fmt, logs, out);
            out.flush();
        } catch (Exception e) {
            log.error("导出操作日志失败 format={} count={}", format, logs.size(), e);
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    /** 解析逗号分隔的 ID 列表，忽略空白和非数字项 */
    private List<Long> parseIds(String ids) {
        if (ids == null || ids.isBlank()) {
            return List.of();
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.matches("\\d+"))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /** 构造下载文件名：操作日志_20260731_153000.xlsx */
    private String buildFileName(OperationLogExporter.Format fmt) {
        String ext = switch (fmt) {
            case EXCEL -> "xlsx";
            case PDF -> "pdf";
            case MARKDOWN -> "md";
        };
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "操作日志_" + timestamp + "." + ext;
    }

    /** 设置响应头：Content-Type / Content-Disposition（中文文件名用 UTF-8 编码） */
    private void setResponseHeaders(HttpServletResponse response, OperationLogExporter.Format fmt, String fileName) {
        String contentType = switch (fmt) {
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case PDF -> "application/pdf";
            case MARKDOWN -> "text/markdown; charset=utf-8";
        };
        response.setContentType(contentType);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // RFC 5987 编码中文文件名，兼容主流浏览器
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
        // 允许前端 axios 携带 token 跨域时暴露该头
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}
