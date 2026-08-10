package com.car.rental.module.marketing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Coupon;
import com.car.rental.entity.MemberCoupon;

import java.util.List;

public interface CouponService {

    /** 分页查询（含已投放筛选；查询结果自动填充关联车辆名称） */
    IPage<Coupon> getList(long pageNum, long pageSize, String name, String type, String status, Integer published);

    /** 详情（含关联车辆ID/名称） */
    Coupon getById(Long id);

    /** 新增（默认草稿，需二次确认投放） */
    void add(Coupon coupon);

    /** 修改（已投放不可改关键字段） */
    void update(Coupon coupon);

    /** 删除（已投放不可删） */
    void delete(Long id);

    /** 确认投放：草稿/下线 → 已投放，二次确认防止误发 */
    void publish(Long id);

    /** 下线：已投放 → 已下线 */
    void offline(Long id);

    /** 保存关联车辆（一对多，先清后插） */
    void saveCars(Long couponId, List<Long> carIds);

    /** 查询关联车辆ID列表 */
    List<Long> listCarIds(Long couponId);

    /** 领取记录（跨库查 member_coupon） */
    List<MemberCoupon> listReceiveRecords(Long couponId);

    /** 兼容旧状态切换接口（published→publish, offline→offline） */
    void toggleStatus(Long id, String status);
}
