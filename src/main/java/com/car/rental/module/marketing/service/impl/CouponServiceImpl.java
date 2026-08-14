package com.car.rental.module.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.CouponCar;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.entity.MemberCoupon;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.marketing.mapper.CouponCarMapper;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.mapper.MemberCouponMapper;
import com.car.rental.module.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final CouponCarMapper couponCarMapper;
    private final MemberCouponMapper memberCouponMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final SecurityUtil securityUtil;

    private static final List<String> VALID_TYPES = Arrays.asList("discount", "deduction", "duration");
    private static final List<String> VALID_SCOPES = Arrays.asList("all", "specified");

    /** 业务状态枚举（动态计算得出，非持久化） */
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_PUBLISHED = "published";
    private static final String STATUS_SOLD_OUT = "sold_out";
    private static final String STATUS_EXPIRED = "expired";
    private static final String STATUS_OFFLINE = "offline";

    /** 需要动态计算的派生状态（查询时过滤需要内存过滤） */
    private static final java.util.Set<String> DYNAMIC_STATUSES = java.util.Set.of(
            STATUS_PENDING, STATUS_PUBLISHED, STATUS_SOLD_OUT, STATUS_EXPIRED);

    @Override
    public IPage<Coupon> getList(long pageNum, long pageSize, String name, String type, String status, Integer published, Integer stackable) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) wrapper.like(Coupon::getName, name);
        if (StringUtils.hasText(type)) wrapper.eq(Coupon::getType, type);
        if (stackable != null) wrapper.eq(Coupon::getStackable, stackable);
        // status 过滤策略：
        //  - draft/offline：直接查持久化字段
        //  - pending/published/sold_out/expired：先查 status=published（这些是派生状态），再内存过滤
        if (StringUtils.hasText(status)) {
            if (DYNAMIC_STATUSES.contains(status)) {
                wrapper.eq(Coupon::getStatus, STATUS_PUBLISHED);
            } else {
                wrapper.eq(Coupon::getStatus, status);
            }
        }
        if (published != null) wrapper.eq(Coupon::getPublished, published);
        wrapper.orderByDesc(Coupon::getCreatedAt);
        IPage<Coupon> result = couponMapper.selectPage(page, wrapper);

        LocalDateTime now = LocalDateTime.now();
        java.util.List<Coupon> filtered = new java.util.ArrayList<>();
        for (Coupon c : result.getRecords()) {
            // 填充关联车辆名称
            if ("specified".equals(c.getApplyScope())) {
                c.setCarNames(couponMapper.selectCarNamesByCouponId(c.getId()));
            }
            // 动态计算业务状态（覆盖原 status 字段返回给前端）
            String effectiveStatus = computeEffectiveStatus(c, now);
            c.setStatus(effectiveStatus);
            // 内存过滤派生状态
            if (StringUtils.hasText(status) && DYNAMIC_STATUSES.contains(status) && !status.equals(effectiveStatus)) {
                continue;
            }
            filtered.add(c);
        }
        // 重置分页结果（派生状态过滤后总数可能减少）
        result.setRecords(filtered);
        if (StringUtils.hasText(status) && DYNAMIC_STATUSES.contains(status)) {
            // 派生状态过滤后无法精确分页，重算 total（接受轻微性能开销）
            // 注：此场景下 page 查询已限定 status=published，重新计数代价可控
            result.setTotal(filtered.size());
        }
        return result;
    }

    /**
     * 根据有效期与库存动态计算券的业务状态
     * 仅当持久化 status=published 时才需要派生，draft/offline 直接返回原值
     */
    private String computeEffectiveStatus(Coupon c, LocalDateTime now) {
        if (!STATUS_PUBLISHED.equals(c.getStatus())) {
            return c.getStatus(); // draft / offline 直接返回
        }
        // 已过有效期 → expired
        if (c.getValidEndTime() != null && c.getValidEndTime().isBefore(now)) {
            return STATUS_EXPIRED;
        }
        // 未到生效时间 → pending
        if (c.getValidStartTime() != null && c.getValidStartTime().isAfter(now)) {
            return STATUS_PENDING;
        }
        // 库存领完（-1 表示无限库存，永不售罄）
        if (c.getTotalCount() != null && c.getTotalCount() != -1
                && c.getReceivedCount() != null && c.getReceivedCount() >= c.getTotalCount()) {
            return STATUS_SOLD_OUT;
        }
        return STATUS_PUBLISHED;
    }

    @Override
    public Coupon getById(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        coupon.setCarIds(couponMapper.selectCarIdsByCouponId(id));
        coupon.setCarNames(couponMapper.selectCarNamesByCouponId(id));
        // 详情同样返回动态计算后的业务状态
        coupon.setStatus(computeEffectiveStatus(coupon, LocalDateTime.now()));
        return coupon;
    }

    @Override
    @Transactional
    public void add(Coupon coupon) {
        validateCoupon(coupon);
        if (!StringUtils.hasText(coupon.getCode())) {
            coupon.setCode(generateCode());
        }
        coupon.setStatus("draft");
        coupon.setPublished(0);
        coupon.setReceivedCount(0);
        coupon.setUsedCount(0);
        if (coupon.getPerUserLimit() == null) coupon.setPerUserLimit(1);
        if (coupon.getApplyScope() == null) coupon.setApplyScope("all");
        if (coupon.getStackable() == null) coupon.setStackable(0);
        if (coupon.getVersion() == null) coupon.setVersion(0);
        coupon.setCreatedBy(securityUtil.getCurrentUserId());
        coupon.setCreatedAt(LocalDateTime.now());
        couponMapper.insert(coupon);
        saveCars(coupon.getId(), coupon.getCarIds());
    }

    @Override
    @Transactional
    public void update(Coupon coupon) {
        Coupon existing = couponMapper.selectById(coupon.getId());
        if (existing == null) {
            throw new BusinessException("优惠券不存在");
        }
        if ("published".equals(existing.getStatus())) {
            throw new BusinessException("已投放的优惠券不可修改关键字段，请先下线");
        }
        validateCoupon(coupon);
        existing.setName(coupon.getName());
        existing.setType(coupon.getType());
        existing.setTypeName(coupon.getTypeName());
        existing.setValue(coupon.getValue());
        existing.setMinAmount(coupon.getMinAmount());
        existing.setDiscountCap(coupon.getDiscountCap());
        existing.setTotalCount(coupon.getTotalCount());
        existing.setPerUserLimit(coupon.getPerUserLimit());
        existing.setApplyScope(coupon.getApplyScope());
        existing.setStackable(coupon.getStackable());
        existing.setValidStartTime(coupon.getValidStartTime());
        existing.setValidEndTime(coupon.getValidEndTime());
        existing.setRemark(coupon.getRemark());
        existing.setUpdatedBy(securityUtil.getCurrentUserId());
        couponMapper.updateById(existing);
        // 指定车辆才保存关联；全场券清空关联
        List<Long> carIds = "specified".equals(coupon.getApplyScope()) ? coupon.getCarIds() : null;
        saveCars(coupon.getId(), carIds);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if ("published".equals(coupon.getStatus())) {
            throw new BusinessException("已投放的优惠券不可删除，请先下线");
        }
        if (coupon.getReceivedCount() != null && coupon.getReceivedCount() > 0) {
            throw new BusinessException("已被领取的优惠券不可删除");
        }
        couponMapper.deleteById(id);
        couponCarMapper.delete(new LambdaQueryWrapper<CouponCar>().eq(CouponCar::getCouponId, id));
    }

    @Override
    @Transactional
    public void publish(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!"draft".equals(coupon.getStatus()) && !"offline".equals(coupon.getStatus())) {
            throw new BusinessException("仅草稿/已下线状态的优惠券可投放");
        }
        if (coupon.getValidEndTime() != null && coupon.getValidEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过有效期，不可投放");
        }
        coupon.setStatus("published");
        coupon.setPublished(1);
        coupon.setPublishedAt(LocalDateTime.now());
        coupon.setPublishedBy(securityUtil.getCurrentUserId());
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional
    public void offline(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!"published".equals(coupon.getStatus())) {
            throw new BusinessException("仅已投放的优惠券可下线");
        }
        coupon.setStatus("offline");
        coupon.setPublished(0);
        coupon.setUpdatedBy(securityUtil.getCurrentUserId());
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional
    public void saveCars(Long couponId, List<Long> carIds) {
        couponCarMapper.delete(new LambdaQueryWrapper<CouponCar>().eq(CouponCar::getCouponId, couponId));
        if (carIds == null || carIds.isEmpty()) {
            return;
        }
        for (Long carId : carIds) {
            CouponCar cc = new CouponCar();
            cc.setCouponId(couponId);
            cc.setCarId(carId);
            cc.setCreatedAt(LocalDateTime.now());
            couponCarMapper.insert(cc);
        }
    }

    @Override
    public List<Long> listCarIds(Long couponId) {
        return couponMapper.selectCarIdsByCouponId(couponId);
    }

    @Override
    public List<MemberCoupon> listReceiveRecords(Long couponId) {
        return memberCouponMapper.selectList(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getCouponId, couponId)
                .orderByDesc(MemberCoupon::getClaimTime));
    }

    @Override
    public Map<String, Object> listUsedOrders(Long couponId) {
        // 直接查 customer_order 表，通过 coupon_id 关联
        // 不依赖 coupon 表状态，即使优惠券被删除/到期，订单中的关联关系仍持久存在
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerOrder::getCouponId, couponId)
                .orderByDesc(CustomerOrder::getCreateTime);
        List<CustomerOrder> orders = customerOrderMapper.selectList(wrapper);

        // 统计汇总（仅 completed 订单计入优惠金额统计）
        int totalOrders = orders.size();
        int completedOrders = 0;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (CustomerOrder order : orders) {
            if ("completed".equals(order.getStatus())) {
                completedOrders++;
                if (order.getCouponDiscount() != null) {
                    totalDiscount = totalDiscount.add(order.getCouponDiscount());
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orders", orders);
        result.put("totalOrders", totalOrders);
        result.put("completedOrders", completedOrders);
        result.put("totalDiscount", totalDiscount);
        return result;
    }

    @Override
    @Transactional
    public void toggleStatus(Long id, String status) {
        if ("published".equals(status)) {
            publish(id);
        } else if ("offline".equals(status)) {
            offline(id);
        } else {
            throw new BusinessException("不支持的状态: " + status + "（仅支持 published/offline）");
        }
    }

    private void validateCoupon(Coupon c) {
        if (!StringUtils.hasText(c.getName())) {
            throw new BusinessException("券名称不能为空");
        }
        if (!StringUtils.hasText(c.getType())) {
            throw new BusinessException("券类型不能为空");
        }
        if (!VALID_TYPES.contains(c.getType())) {
            throw new BusinessException("券类型非法，仅支持 discount/deduction/duration");
        }
        if (c.getValue() == null || c.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("优惠值非法");
        }
        if ("discount".equals(c.getType())
                && (c.getValue().compareTo(BigDecimal.ONE) > 0 || c.getValue().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BusinessException("折扣值必须在 0~1 之间（如 0.88 表示88折）");
        }
        if (c.getTotalCount() == null || c.getTotalCount() < -1) {
            throw new BusinessException("发放总量非法（-1 表示无限）");
        }
        if (c.getMinAmount() == null || c.getMinAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("最低消费金额非法");
        }
        if (c.getValidStartTime() == null || c.getValidEndTime() == null) {
            throw new BusinessException("有效期不能为空");
        }
        if (c.getValidEndTime().isBefore(c.getValidStartTime())) {
            throw new BusinessException("失效时间不能早于生效时间");
        }
        if (c.getPerUserLimit() == null || c.getPerUserLimit() < 1) {
            throw new BusinessException("每人限领张数至少为1");
        }
        if (!VALID_SCOPES.contains(c.getApplyScope())) {
            throw new BusinessException("适用范围非法，仅支持 all/specified");
        }
        if ("specified".equals(c.getApplyScope()) && (c.getCarIds() == null || c.getCarIds().isEmpty())) {
            throw new BusinessException("指定车辆券必须关联至少一辆车");
        }
    }

    private String generateCode() {
        return "CP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
