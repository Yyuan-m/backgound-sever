package com.car.rental.module.after_sales.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.after_sales.service.AfterSalesComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "售后投诉管理", description = "售后投诉工单的查询、登记、编辑、处理流转、优先级调整与删除，并支持查询工单关联车辆信息")
@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSalesComplaintController {

    private final AfterSalesComplaintService complaintService;

    @Operation(summary = "投诉工单列表（分页）", description = "支持按工单号（模糊）、类型、状态、优先级筛选，按创建时间倒序，每条记录自动回填关联车辆摘要（订单→车辆）。需要 after_sales:complaint 权限")
    @GetMapping("/list")
    @RequirePermission("after_sales:complaint")
    public Result<PageResult<AfterSalesComplaint>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "工单号（模糊匹配）") @RequestParam(required = false) String ticketNo,
            @Parameter(description = "投诉类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态筛选（pending 待处理 / processing 处理中 / resolved 已解决 / rejected 已驳回）") @RequestParam(required = false) String status,
            @Parameter(description = "优先级") @RequestParam(required = false) String priority) {
        IPage<AfterSalesComplaint> page = complaintService.getPageList(pageNum, pageSize, ticketNo, type, status, priority);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "投诉工单详情", description = "按 ID 查询单条投诉工单，工单不存在时报错。需要 after_sales:complaint 权限")
    @GetMapping("/{id}")
    @RequirePermission("after_sales:complaint")
    public Result<?> getById(@Parameter(description = "工单ID") @PathVariable Long id) {
        return Result.ok(complaintService.getById(id));
    }

    @Operation(summary = "投诉关联车辆详情", description = "根据工单 ID 沿订单→车辆链路查询关联车辆信息，未关联订单或车辆时返回空。需要 after_sales:complaint 权限")
    @GetMapping("/{id}/vehicle")
    @RequirePermission("after_sales:complaint")
    public Result<?> getVehicle(@Parameter(description = "工单ID") @PathVariable Long id) {
        return Result.ok(complaintService.getVehicleByComplaintId(id));
    }

    @Operation(summary = "新增投诉工单", description = "登记新的售后投诉工单，创建时间由后端自动写入。需要 after_sales:complaint:add 权限")
    @PostMapping("/add")
    @RequirePermission("after_sales:complaint:add")
    @LogChanges(
        entityClass = AfterSalesComplaint.class,
        mapperClass = AfterSalesComplaintMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "id:ID", "ticketNo:工单号", "orderId:订单ID", "orderNo:订单号",
            "customerName:客户姓名", "type:类型", "typeName:类型名称",
            "description:描述", "priority:优先级", "status:状态",
            "assignee:处理人", "solution:解决方案", "satisfaction:满意度",
            "createdAt:创建时间", "resolvedAt:解决时间"
        }
    )
    public Result<?> add(@RequestBody AfterSalesComplaint complaint) {
        complaintService.addComplaint(complaint);
        return Result.ok();
    }

    @Operation(summary = "编辑投诉工单", description = "按 ID 更新工单号、关联订单、客户、类型、描述、优先级等基本信息；status 传值时一并更新，工单不存在时报错。需要 after_sales:complaint:update 权限")
    @PutMapping("/update")
    @RequirePermission("after_sales:complaint:update")
    @LogChanges(
        entityClass = AfterSalesComplaint.class,
        mapperClass = AfterSalesComplaintMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "id:ID", "ticketNo:工单号", "orderId:订单ID", "orderNo:订单号",
            "customerName:客户姓名", "type:类型", "typeName:类型名称",
            "description:描述", "priority:优先级", "status:状态",
            "assignee:处理人", "solution:解决方案", "satisfaction:满意度",
            "createdAt:创建时间", "resolvedAt:解决时间"
        }
    )
    public Result<?> update(@RequestBody AfterSalesComplaint complaint) {
        complaintService.updateComplaint(complaint);
        return Result.ok();
    }

    @Operation(summary = "删除投诉工单", description = "按 ID 删除投诉工单，工单不存在时报错。需要 after_sales:complaint:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("after_sales:complaint:delete")
    @LogChanges(
        entityClass = AfterSalesComplaint.class,
        mapperClass = AfterSalesComplaintMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "id:ID", "ticketNo:工单号", "orderId:订单ID", "orderNo:订单号",
            "customerName:客户姓名", "type:类型", "typeName:类型名称",
            "description:描述", "priority:优先级", "status:状态",
            "assignee:处理人", "solution:解决方案", "satisfaction:满意度",
            "createdAt:创建时间", "resolvedAt:解决时间"
        }
    )
    public Result<?> delete(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return Result.ok();
    }

    @Operation(summary = "处理投诉工单", description = "更新工单状态、处理人、解决方案与满意度；处理人由后端取当前登录人昵称/用户名，不信任前端回传值。需要 after_sales:complaint:handle 权限")
    @PutMapping("/{id}/handle")
    @RequirePermission("after_sales:complaint:handle")
    public Result<?> handle(@Parameter(description = "工单ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = (String) params.get("status");
        String assignee = (String) params.get("assignee");
        String solution = (String) params.get("solution");
        Integer satisfaction = params.get("satisfaction") != null ? ((Number) params.get("satisfaction")).intValue() : null;
        complaintService.handleComplaint(id, status, assignee, solution, satisfaction);
        return Result.ok();
    }

    @Operation(summary = "开始处理工单", description = "将工单置为处理中并记录当前操作人为处理人；已终态（resolved 已解决 / rejected 已驳回）的工单不允许再开始处理。需要 after_sales:complaint:handle 权限")
    @PutMapping("/{id}/processing")
    @RequirePermission("after_sales:complaint:handle")
    public Result<?> startProcessing(@Parameter(description = "工单ID") @PathVariable Long id) {
        complaintService.startProcessing(id);
        return Result.ok();
    }

    @Operation(summary = "快捷调整优先级", description = "按工单 ID 修改优先级，优先级不能为空，工单不存在时报错。需要 after_sales:complaint:update 权限")
    @PutMapping("/{id}/priority")
    @RequirePermission("after_sales:complaint:update")
    public Result<?> updatePriority(@Parameter(description = "工单ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        String priority = (String) params.get("priority");
        complaintService.updatePriority(id, priority);
        return Result.ok();
    }
}
