package com.car.rental.module.after_sales.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.after_sales.service.AfterSalesComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSalesComplaintController {

    private final AfterSalesComplaintService complaintService;

    @GetMapping("/list")
    @RequirePermission("after_sales:complaint")
    public Result<PageResult<AfterSalesComplaint>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String ticketNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        IPage<AfterSalesComplaint> page = complaintService.getPageList(pageNum, pageSize, ticketNo, type, status, priority);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("after_sales:complaint")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(complaintService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("after_sales:complaint")
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

    @PutMapping("/update")
    @RequirePermission("after_sales:complaint")
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

    @DeleteMapping("/{id}")
    @RequirePermission("after_sales:complaint")
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

    @PutMapping("/{id}/handle")
    @RequirePermission("after_sales:complaint")
    public Result<?> handle(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = (String) params.get("status");
        String assignee = (String) params.get("assignee");
        String solution = (String) params.get("solution");
        Integer satisfaction = params.get("satisfaction") != null ? ((Number) params.get("satisfaction")).intValue() : null;
        complaintService.handleComplaint(id, status, assignee, solution, satisfaction);
        return Result.ok();
    }
}