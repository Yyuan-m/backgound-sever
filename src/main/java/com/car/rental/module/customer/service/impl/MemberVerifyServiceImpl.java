package com.car.rental.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.MemberVerifyRecord;
import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.MemberVerifyMapper;
import com.car.rental.module.customer.service.MemberVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberVerifyServiceImpl implements MemberVerifyService {

    private final MemberVerifyMapper memberVerifyMapper;
    private final CustomerInfoMapper customerInfoMapper;
    private final SecurityUtil securityUtil;

    @Override
    public IPage<MemberVerifyRecord> getVerifyList(long pageNum, long pageSize, String status, String keyword) {
        Page<MemberVerifyRecord> page = new Page<>(pageNum, pageSize);
        return memberVerifyMapper.selectVerifyPage(page, status, keyword);
    }

    @Override
    public Map<String, Object> getVerifyStats() {
        return memberVerifyMapper.selectVerifyStats();
    }

    @Override
    public MemberVerifyRecord getVerifyDetail(Long id) {
        MemberVerifyRecord record = memberVerifyMapper.selectVerifyDetail(id);
        if (record == null) {
            throw new BusinessException("认证记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void review(Long recordId, boolean approved, String rejectReason) {
        MemberVerifyRecord record = memberVerifyMapper.selectVerifyDetail(recordId);
        if (record == null) {
            throw new BusinessException("认证记录不存在");
        }
        if (!"pending".equals(record.getStatus())) {
            throw new BusinessException("该记录已审核，请勿重复操作");
        }
        if (!approved && (rejectReason == null || rejectReason.isBlank())) {
            throw new BusinessException("驳回时必须填写驳回原因");
        }

        String reviewer = securityUtil.getCurrentUsername();

        // 1. 更新认证记录
        memberVerifyMapper.updateReview(recordId,
                approved ? "approved" : "rejected",
                approved ? null : rejectReason.trim(),
                reviewer);

        // 2. 同步 member 表认证状态
        memberVerifyMapper.updateMemberVerifyStatus(record.getMemberId(),
                approved ? "verified" : "rejected",
                approved ? null : rejectReason.trim());

        // 3. 同步 customer_info.real_name_status（1已认证/3认证失败），无记录则插入
        int updated = memberVerifyMapper.updateCustomerInfoRealNameStatus(record.getMemberId(),
                approved ? 1 : 3);
        if (updated == 0) {
            upsertCustomerInfo(record, approved ? 1 : 3);
        }
    }

    /** customer_info 无该会员记录时插入一条基础记录（对齐 CustomerServiceImpl.verifyRealName 的做法） */
    private void upsertCustomerInfo(MemberVerifyRecord record, int realNameStatus) {
        Map<String, Object> member = memberVerifyMapper.selectMemberById(record.getMemberId());
        if (member == null) {
            return;
        }
        String phone = (String) member.get("phone");
        // 可能已被并发插入，先查一次
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getPhone, phone);
        wrapper.last("LIMIT 1");
        CustomerInfo existing = customerInfoMapper.selectOne(wrapper);
        if (existing != null) {
            CustomerInfo update = new CustomerInfo();
            update.setId(existing.getId());
            update.setRealNameStatus(realNameStatus);
            update.setUpdatedAt(LocalDateTime.now());
            customerInfoMapper.updateById(update);
            return;
        }
        String name = record.getRealName() != null ? record.getRealName() : (String) member.get("nickname");
        CustomerInfo newRecord = new CustomerInfo();
        newRecord.setPhone(phone);
        newRecord.setName(name != null ? name : ("用户" + phone.substring(Math.max(0, phone.length() - 4))));
        newRecord.setMembershipLevel((String) member.get("level"));
        newRecord.setMembershipName((String) member.get("levelName"));
        newRecord.setCreditScore(100);
        newRecord.setTotalOrders(0);
        newRecord.setTotalSpent(BigDecimal.ZERO);
        newRecord.setDiscount(BigDecimal.ONE);
        newRecord.setIsBlacklist(0);
        newRecord.setRealNameStatus(realNameStatus);
        newRecord.setStatus(1);
        newRecord.setCreatedAt(LocalDateTime.now());
        customerInfoMapper.insert(newRecord);
    }
}
