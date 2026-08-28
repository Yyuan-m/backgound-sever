package com.car.rental.module.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.MemberVerifyRecord;

import java.util.Map;

public interface MemberVerifyService {

    /** 分页查询认证记录（status: pending/approved/rejected，null 全部） */
    IPage<MemberVerifyRecord> getVerifyList(long pageNum, long pageSize, String status, String keyword);

    /** 审核统计：total/pending/approved/rejected/today 数量 */
    Map<String, Object> getVerifyStats();

    /** 认证记录详情 */
    MemberVerifyRecord getVerifyDetail(Long id);

    /**
     * 人工审核：通过则 member.verify_status=verified；驳回则 rejected 并记录原因。
     * 同步更新 customer_info.real_name_status（1已认证/3认证失败）。
     */
    void review(Long recordId, boolean approved, String rejectReason);
}
