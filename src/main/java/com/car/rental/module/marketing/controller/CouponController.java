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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "优惠券管理", description = "优惠券增删改查、投放/下线（二次确认防误发）、关联车辆与领取/核销记录查询")
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

    @Operation(summary = "优惠券列表（分页）", description = "结果自动填充关联车辆名称并动态计算业务状态（已投放的券按有效期/库存派生 pending/published/sold_out/expired，派生状态为内存过滤）；totalCount=-1 表示无限库存。需要 marketing:coupon 权限")
    @GetMapping("/list")
    @RequirePermission("marketing:coupon")
    public Result<PageResult<Coupon>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "优惠券名称模糊匹配") @RequestParam(required = false) String name,
            @Parameter(description = "券类型（discount 折扣 / deduction 满减 / duration 时长）") @RequestParam(required = false) String type,
            @Parameter(description = "业务状态（draft 草稿 / pending 待投放 / published 已投放 / sold_out 已抢光 / expired 已过期 / offline 已下线）") @RequestParam(required = false) String status,
            @Parameter(description = "投放标志（1 已投放 / 0 未投放）") @RequestParam(required = false) Integer published,
            @Parameter(description = "是否可叠加（1 可叠加 / 0 不可）") @RequestParam(required = false) Integer stackable) {
        IPage<Coupon> page = couponService.getList(pageNum, pageSize, name, type, status, published, stackable);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "优惠券详情", description = "含关联车辆 ID 与名称。需要 marketing:coupon 权限")
    @GetMapping("/{id}")
    @RequirePermission("marketing:coupon")
    public Result<Coupon> getById(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.ok(couponService.getById(id));
    }

    @Operation(summary = "新增优惠券", description = "新增后默认为草稿状态，需调用投放接口二次确认后才对 C 端可见。需要 marketing:coupon:add 权限")
    @PostMapping("/add")
    @RequirePermission("marketing:coupon:add")
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

    @Operation(summary = "编辑优惠券", description = "按 body 中的 ID 更新；已投放的券不可修改关键字段。需要 marketing:coupon:update 权限")
    @PutMapping("/update")
    @RequirePermission("marketing:coupon:update")
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

    @Operation(summary = "删除优惠券", description = "按 ID 删除；已投放的券不可删除。需要 marketing:coupon:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("marketing:coupon:delete")
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
    public Result<Void> delete(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.delete(id);
        return Result.ok();
    }

    /** 确认投放（草稿/已下线 → 已投放），防止运营误发的二次确认 */
    @Operation(summary = "确认投放优惠券", description = "草稿/已下线 → 已投放，二次确认防止运营误发；投放后 C 端可见可领取。需要 marketing:coupon:update 权限")
    @PutMapping("/{id}/publish")
    @RequirePermission("marketing:coupon:update")
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
    public Result<Void> publish(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.publish(id);
        return Result.ok();
    }

    /** 下线（已投放 → 已下线） */
    @Operation(summary = "下线优惠券", description = "已投放 → 已下线，下线后 C 端不可再领取。需要 marketing:coupon:update 权限")
    @PutMapping("/{id}/offline")
    @RequirePermission("marketing:coupon:update")
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
    public Result<Void> offline(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.offline(id);
        return Result.ok();
    }

    /** 兼容旧状态切换接口（status=published→投放, offline→下线） */
    @Operation(summary = "切换优惠券状态（旧接口）", description = "兼容旧状态切换：status=published 投放 / offline 下线，请求体为 {\"status\": \"...\"}。需要 marketing:coupon:status 权限")
    @PutMapping("/{id}/status")
    @RequirePermission("marketing:coupon:status")
    public Result<Void> toggleStatus(@Parameter(description = "优惠券ID") @PathVariable Long id, @RequestBody Map<String, String> body) {
        couponService.toggleStatus(id, body.get("status"));
        return Result.ok();
    }

    /** 设置关联车辆（一对多），body: {"carIds":[1,2,3]} */
    @Operation(summary = "设置关联车辆", description = "保存券与车辆的关联关系（一对多，先清后插，仅适用范围 specified 的券），请求体为 {\"carIds\":[1,2,3]}。需要 marketing:coupon:update 权限")
    @PutMapping("/{id}/cars")
    @RequirePermission("marketing:coupon:update")
    public Result<Void> saveCars(@Parameter(description = "优惠券ID") @PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        couponService.saveCars(id, body.get("carIds"));
        return Result.ok();
    }

    /** 查询关联车辆ID列表 */
    @Operation(summary = "查询关联车辆ID列表", description = "返回该券关联的车辆 ID 列表。需要 marketing:coupon 权限")
    @GetMapping("/{id}/cars")
    @RequirePermission("marketing:coupon")
    public Result<List<Long>> listCarIds(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.ok(couponService.listCarIds(id));
    }

    /** 领取记录（跨库查 member_coupon） */
    @Operation(summary = "领取记录", description = "跨库查询该券的会员领取记录（member_coupon）。需要 marketing:coupon 权限")
    @GetMapping("/{id}/receive-records")
    @RequirePermission("marketing:coupon")
    public Result<List<MemberCoupon>> listReceiveRecords(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.ok(couponService.listReceiveRecords(id));
    }

    /** 关联订单（查 customer_order WHERE coupon_id=?，含统计汇总） */
    @Operation(summary = "关联订单与使用统计", description = "查询使用该券的订单列表及统计汇总（总订单数/完成订单数/累计优惠金额）；数据源为订单表，券被删除/到期后关联关系仍保留。需要 marketing:coupon 权限")
    @GetMapping("/{id}/used-orders")
    @RequirePermission("marketing:coupon")
    public Result<java.util.Map<String, Object>> listUsedOrders(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.ok(couponService.listUsedOrders(id));
    }
}
