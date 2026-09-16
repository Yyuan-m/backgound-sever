package com.car.rental.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "订单管理", description = "订单增删改查、状态流转与财务维护；订单完成时自动生成发票并写入财务流水")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "订单列表（分页）", description = "关键字匹配订单号/联系人姓名/联系电话/车辆名称；日期按创建时间过滤；结果含车辆明细 items，按创建时间倒序。需要 order:list 权限")
    @GetMapping("/list")
    @RequirePermission("order:list")
    public Result<PageResult<CustomerOrder>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：订单号/联系人姓名/联系电话/车辆名称模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "订单状态（pending 待支付 / renting 租赁中 / completed 已完成 / cancelled 已取消）") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期，格式 yyyy-MM-dd，按创建时间过滤") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式 yyyy-MM-dd，按创建时间过滤（含当天）") @RequestParam(required = false) String endDate) {
        IPage<CustomerOrder> page = orderService.getOrderList(pageNum, pageSize, keyword, status, startDate, endDate);
        return Result.ok(PageResult.of(page));
    }

    /**
     * 按状态统计订单数量（全量，不受分页/筛选条件影响）
     * 供前端 tab 角标展示
     */
    @Operation(summary = "按状态统计订单数量", description = "全量统计各状态订单数（pending/renting/completed/cancelled），不受分页/筛选条件影响，供前端 tab 角标展示。需要 order:list 权限")
    @GetMapping("/status-count")
    @RequirePermission("order:list")
    public Result<Map<String, Long>> statusCount() {
        return Result.ok(orderService.getStatusCount());
    }

    @Operation(summary = "订单详情", description = "含订单车辆明细 items（多车订单），订单不存在时抛业务异常。需要 order:detail 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("order:detail")
    public Result<CustomerOrder> detail(@Parameter(description = "订单ID") @PathVariable Long id) {
        CustomerOrder order = orderService.getOrderDetail(id);
        return Result.ok(order);
    }

    @Operation(summary = "新增订单", description = "订单号不传自动生成；状态默认 pending（待支付）；优惠券折扣金额由 C 端下单流程预先计算后传入；支持 items 多车明细。需要 order:add 权限")
    @PostMapping("/add")
    @RequirePermission("order:add")
    @LogChanges(
        entityClass = CustomerOrder.class,
        mapperClass = CustomerOrderMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createTime", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "memberId:会员ID", "carId:车辆ID",
            "carName:车辆名称", "carCover:车辆封面",
            "status:状态", "statusName:状态名称",
            "startDate:开始日期", "endDate:结束日期", "days:天数",
            "dailyPrice:日租金", "rentAmount:租金总额",
            "couponDiscount:优惠券折扣",
            "couponId:优惠券ID", "couponUserId:用户券记录ID",
            "totalAmount:总金额", "city:城市", "store:门店",
            "contactName:联系人姓名", "contactPhone:联系人电话"
        }
    )
    public Result<Void> add(@RequestBody CustomerOrder order) {
        orderService.addOrder(order);
        return Result.ok();
    }

    @Operation(summary = "编辑订单", description = "按 ID 更新订单信息及车辆明细。需要 order:update 权限")
    @PutMapping("/update/{id}")
    @RequirePermission("order:update")
    @LogChanges(
        entityClass = CustomerOrder.class,
        mapperClass = CustomerOrderMapper.class,
        ignoreFields = {"createTime", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "memberId:会员ID", "carId:车辆ID",
            "carName:车辆名称", "carCover:车辆封面",
            "status:状态", "statusName:状态名称",
            "startDate:开始日期", "endDate:结束日期", "days:天数",
            "dailyPrice:日租金", "rentAmount:租金总额",
            "couponDiscount:优惠券折扣",
            "couponId:优惠券ID", "couponUserId:用户券记录ID",
            "totalAmount:总金额", "city:城市", "store:门店",
            "contactName:联系人姓名", "contactPhone:联系人电话"
        }
    )
    public Result<Void> update(@Parameter(description = "订单ID") @PathVariable Long id, @RequestBody CustomerOrder order) {
        orderService.updateOrder(id, order);
        return Result.ok();
    }

    @Operation(summary = "删除订单", description = "按 ID 逻辑删除订单。需要 order:delete 权限")
    @DeleteMapping("/delete/{id}")
    @RequirePermission("order:delete")
    @LogChanges(
        entityClass = CustomerOrder.class,
        mapperClass = CustomerOrderMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createTime", "isDelete"},
        fieldLabels = {
            "orderNo:订单号", "memberId:会员ID", "carId:车辆ID",
            "carName:车辆名称", "carCover:车辆封面",
            "status:状态", "statusName:状态名称",
            "startDate:开始日期", "endDate:结束日期", "days:天数",
            "dailyPrice:日租金", "rentAmount:租金总额",
            "couponDiscount:优惠券折扣",
            "couponId:优惠券ID", "couponUserId:用户券记录ID",
            "totalAmount:总金额", "city:城市", "store:门店",
            "contactName:联系人姓名", "contactPhone:联系人电话"
        }
    )
    public Result<Void> delete(@Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.ok();
    }

    @Operation(summary = "更新订单状态", description = "状态值：pending 待支付 / renting 租赁中 / completed 已完成 / cancelled 已取消。改为 completed 时自动生成发票（pending 状态、金额=租金总额）、写入租金收入与租赁成本财务流水并核销锁定的优惠券（核销失败回滚整个事务）；改为 cancelled 时释放锁定的优惠券（失败不阻断取消）。需要 order:status 权限")
    @PutMapping("/status/{id}")
    @RequirePermission("order:status")
    public Result<Void> status(@Parameter(description = "订单ID") @PathVariable Long id,
                               @Parameter(description = "目标状态：pending 待支付 / renting 租赁中 / completed 已完成 / cancelled 已取消") @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return Result.ok();
    }

    /**
     * 手动触发：自动完成到期订单 + 回补缺失财务流水/发票。
     * 用于应急修复或验证。与定时任务逻辑一致，幂等可重复执行。
     * 返回 {autoCompleted, backfilled} 两个计数。
     */
    @Operation(summary = "财务维护（手动触发）", description = "自动完成到期订单（renting 且结束日期早于今天 → completed）并回补缺失的财务流水与发票，与定时任务逻辑一致、幂等可重复执行，用于应急修复或验证；返回 {autoCompleted, backfilled} 两个计数。需要 order:status 权限")
    @PostMapping("/finance/maintain")
    @RequirePermission("order:status")
    public Result<Map<String, Integer>> financeMaintain() {
        int autoCompleted = orderService.autoCompleteExpiredOrders();
        int backfilled = orderService.backfillMissingFinanceRecords();
        Map<String, Integer> result = new java.util.HashMap<>();
        result.put("autoCompleted", autoCompleted);
        result.put("backfilled", backfilled);
        return Result.ok(result);
    }
}
