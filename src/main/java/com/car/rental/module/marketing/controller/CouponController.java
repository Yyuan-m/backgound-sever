package com.car.rental.module.marketing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.MemberCoupon;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /** 字段标签内联数组：注解属性必须是编译期常量，不能引用 static final 数组变量。 */
    private static final String[] FIELD_LABELS_INLINE = {
            "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
            "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
            "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
            "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
            "validStartTime:生效时间", "validEndTime:失效时间",
            "status:状态", "published:确认投放标志", "publishedAt:投放时间",
            "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
    };

    @GetMapping("/list")
    @RequirePermission("marketing:coupon")
    public Result<PageResult<Coupon>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer published,
            @RequestParam(required = false) Integer stackable) {
        IPage<Coupon> page = couponService.getList(pageNum, pageSize, name, type, status, published, stackable);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("marketing:coupon")
    public Result<Coupon> getById(@PathVariable Long id) {
        return Result.ok(couponService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("marketing:coupon")
    @LogChanges(
            entityClass = Coupon.class,
            mapperClass = CouponMapper.class,
            mode = LogChanges.Mode.ADD,
            ignoreFields = {"createdAt", "receivedCount", "usedCount", "version"},
            fieldLabels = {
                    "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
                    "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
                    "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
                    "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
                    "validStartTime:生效时间", "validEndTime:失效时间",
                    "status:状态", "published:确认投放标志", "publishedAt:投放时间",
                    "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
            }
    )
    public Result<Void> add(@RequestBody Coupon coupon) {
        couponService.add(coupon);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequirePermission("marketing:coupon")
    @LogChanges(
            entityClass = Coupon.class,
            mapperClass = CouponMapper.class,
            ignoreFields = {"createdAt", "receivedCount", "usedCount", "version"},
            fieldLabels = {
                    "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
                    "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
                    "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
                    "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
                    "validStartTime:生效时间", "validEndTime:失效时间",
                    "status:状态", "published:确认投放标志", "publishedAt:投放时间",
                    "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
            }
    )
    public Result<Void> update(@RequestBody Coupon coupon) {
        couponService.update(coupon);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("marketing:coupon")
    @LogChanges(
            entityClass = Coupon.class,
            mapperClass = CouponMapper.class,
            mode = LogChanges.Mode.DELETE,
            ignoreFields = {"createdAt", "version"},
            fieldLabels = {
                    "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
                    "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
                    "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
                    "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
                    "validStartTime:生效时间", "validEndTime:失效时间",
                    "status:状态", "published:确认投放标志", "publishedAt:投放时间",
                    "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
            }
    )
    public Result<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return Result.ok();
    }

    /** 确认投放（草稿/已下线 → 已投放），防止运营误发的二次确认 */
    @PutMapping("/{id}/publish")
    @RequirePermission("marketing:coupon")
    @LogChanges(
            entityClass = Coupon.class,
            mapperClass = CouponMapper.class,
            ignoreFields = {"createdAt", "version"},
            fieldLabels = {
                    "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
                    "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
                    "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
                    "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
                    "validStartTime:生效时间", "validEndTime:失效时间",
                    "status:状态", "published:确认投放标志", "publishedAt:投放时间",
                    "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
            }
    )
    public Result<Void> publish(@PathVariable Long id) {
        couponService.publish(id);
        return Result.ok();
    }

    /** 下线（已投放 → 已下线） */
    @PutMapping("/{id}/offline")
    @RequirePermission("marketing:coupon")
    @LogChanges(
            entityClass = Coupon.class,
            mapperClass = CouponMapper.class,
            ignoreFields = {"createdAt", "version"},
            fieldLabels = {
                    "id:ID", "code:券码", "name:优惠券名称", "type:类型", "typeName:类型名称",
                    "value:面值", "minAmount:最低消费", "discountCap:折扣封顶",
                    "totalCount:发放总量", "receivedCount:已领取数量", "usedCount:已核销数量",
                    "perUserLimit:每人限领", "applyScope:适用范围", "stackable:是否可叠加",
                    "validStartTime:生效时间", "validEndTime:失效时间",
                    "status:状态", "published:确认投放标志", "publishedAt:投放时间",
                    "publishedBy:投放人", "remark:备注", "createdAt:创建时间"
            }
    )
    public Result<Void> offline(@PathVariable Long id) {
        couponService.offline(id);
        return Result.ok();
    }

    /** 兼容旧状态切换接口（status=published→投放, offline→下线） */
    @PutMapping("/{id}/status")
    @RequirePermission("marketing:coupon")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        couponService.toggleStatus(id, body.get("status"));
        return Result.ok();
    }

    /** 设置关联车辆（一对多），body: {"carIds":[1,2,3]} */
    @PutMapping("/{id}/cars")
    @RequirePermission("marketing:coupon")
    public Result<Void> saveCars(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        couponService.saveCars(id, body.get("carIds"));
        return Result.ok();
    }

    /** 查询关联车辆ID列表 */
    @GetMapping("/{id}/cars")
    @RequirePermission("marketing:coupon")
    public Result<List<Long>> listCarIds(@PathVariable Long id) {
        return Result.ok(couponService.listCarIds(id));
    }

    /** 领取记录（跨库查 member_coupon） */
    @GetMapping("/{id}/receive-records")
    @RequirePermission("marketing:coupon")
    public Result<List<MemberCoupon>> listReceiveRecords(@PathVariable Long id) {
        return Result.ok(couponService.listReceiveRecords(id));
    }

    /** 关联订单（查 customer_order WHERE coupon_id=?，含统计汇总） */
    @GetMapping("/{id}/used-orders")
    @RequirePermission("marketing:coupon")
    public Result<java.util.Map<String, Object>> listUsedOrders(@PathVariable Long id) {
        return Result.ok(couponService.listUsedOrders(id));
    }
}
