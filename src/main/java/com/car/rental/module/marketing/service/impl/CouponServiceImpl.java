package com.car.rental.module.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.CouponCar;
import com.car.rental.entity.MemberCoupon;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final CouponCarMapper couponCarMapper;
    private final MemberCouponMapper memberCouponMapper;
    private final SecurityUtil securityUtil;

    private static final List<String> VALID_TYPES = Arrays.asList("discount", "deduction", "duration");
    private static final List<String> VALID_SCOPES = Arrays.asList("all", "specified");

    @Override
    public IPage<Coupon> getList(long pageNum, long pageSize, String name, String type, String status, Integer published) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) wrapper.like(Coupon::getName, name);
        if (StringUtils.hasText(type)) wrapper.eq(Coupon::getType, type);
        if (StringUtils.hasText(status)) wrapper.eq(Coupon::getStatus, status);
        if (published != null) wrapper.eq(Coupon::getPublished, published);
        wrapper.orderByDesc(Coupon::getCreatedAt);
        IPage<Coupon> result = couponMapper.selectPage(page, wrapper);
        // 填充关联车辆名称（指定车辆券）
        for (Coupon c : result.getRecords()) {
            if ("specified".equals(c.getApplyScope())) {
                c.setCarNames(couponMapper.selectCarNamesByCouponId(c.getId()));
            }
        }
        return result;
    }

    @Override
    public Coupon getById(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        coupon.setCarIds(couponMapper.selectCarIdsByCouponId(id));
        coupon.setCarNames(couponMapper.selectCarNamesByCouponId(id));
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
