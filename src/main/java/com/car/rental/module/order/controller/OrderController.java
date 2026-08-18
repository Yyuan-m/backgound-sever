package com.car.rental.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    @RequirePermission("order:list")
    public Result<PageResult<CustomerOrder>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        IPage<CustomerOrder> page = orderService.getOrderList(pageNum, pageSize, keyword, status, startDate, endDate);
        return Result.ok(PageResult.of(page));
    }

    /**
     * 按状态统计订单数量（全量，不受分页/筛选条件影响）
     * 供前端 tab 角标展示
     */
    @GetMapping("/status-count")
    @RequirePermission("order:list")
    public Result<Map<String, Long>> statusCount() {
        return Result.ok(orderService.getStatusCount());
    }

    @GetMapping("/detail/{id}")
    @RequirePermission("order:detail")
    public Result<CustomerOrder> detail(@PathVariable Long id) {
        CustomerOrder order = orderService.getOrderDetail(id);
        return Result.ok(order);
    }

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
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerOrder order) {
        orderService.updateOrder(id, order);
        return Result.ok();
    }

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
    public Result<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    @RequirePermission("order:status")
    public Result<Void> status(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return Result.ok();
    }

    /**
     * 手动触发：自动完成到期订单 + 回补缺失财务流水/发票。
     * 用于应急修复或验证。与定时任务逻辑一致，幂等可重复执行。
     * 返回 {autoCompleted, backfilled} 两个计数。
     */
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
