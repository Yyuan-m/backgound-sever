package com.car.rental.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerInfoMapper customerInfoMapper;
    private final CustomerOrderMapper customerOrderMapper;

    @Override
    public IPage<CustomerInfo> getCustomerList(long pageNum, long pageSize, String keyword, Integer status, String level) {
        Page<CustomerInfo> page = new Page<>(pageNum, pageSize);
        // 以 car_rental_customer.member 为主表，LEFT JOIN customer_info 补充后台特有字段
        return customerInfoMapper.selectPageWithMember(page, keyword, status, level);
    }

    /**
     * 编辑租客信息。
     * 前端传入的 id 实际是 car_rental_customer.member.id（主键展示用）。
     * 后端通过 member.id → phone → customer_info upsert：
     *   - 若 customer_info 中存在对应 phone 的记录，则更新
     *   - 若不存在，则插入一条新记录（phone/姓名/会员等级取自 member 表）
     *
     * @param memberId 官网 member 表主键
     */
    @Override
    public void updateCustomer(Long memberId, CustomerInfo customer) {
        Map<String, Object> member = customerInfoMapper.selectMemberById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        String phone = (String) member.get("phone");

        // 用 phone 查找已有的 customer_info 记录
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getPhone, phone);
        wrapper.last("LIMIT 1");
        CustomerInfo existing = customerInfoMapper.selectOne(wrapper);

        if (existing == null) {
            // customer_info 中没有该会员，插入一条（用 member 表的 phone/name 等基础信息）
            String name = customer.getName() != null ? customer.getName() : (String) member.get("realName");
            CustomerInfo newRecord = new CustomerInfo();
            newRecord.setPhone(phone);
            newRecord.setName(name != null ? name : ("用户" + phone.substring(Math.max(0, phone.length() - 4))));
            newRecord.setMembershipLevel(customer.getMembershipLevel() != null ? customer.getMembershipLevel() : (String) member.get("level"));
            newRecord.setMembershipName(customer.getMembershipName() != null ? customer.getMembershipName() : (String) member.get("levelName"));
            newRecord.setCreditScore(customer.getCreditScore() != null ? customer.getCreditScore() : 100);
            newRecord.setTotalOrders(0);
            newRecord.setTotalSpent(BigDecimal.ZERO);
            newRecord.setDiscount(customer.getDiscount() != null ? customer.getDiscount() : BigDecimal.ONE);
            newRecord.setTags(customer.getTags());
            newRecord.setIsBlacklist(customer.getIsBlacklist() != null ? customer.getIsBlacklist() : 0);
            newRecord.setRealNameStatus(customer.getRealNameStatus() != null ? customer.getRealNameStatus() : 0);
            newRecord.setStatus(customer.getStatus() != null ? customer.getStatus() : 1);
            newRecord.setCreatedAt(LocalDateTime.now());
            customerInfoMapper.insert(newRecord);
        } else {
            // 已存在记录，更新后台可维护字段
            customer.setId(existing.getId());
            customer.setUpdatedAt(LocalDateTime.now());
            // 防止 member 的基础字段被覆盖
            customer.setPhone(phone);
            customerInfoMapper.updateById(customer);
        }
    }

    /**
     * 删除租客（软删除 customer_info）。
     * 前端传入的 id 是 member.id，member 表不删除（官网数据保留）。
     */
    @Override
    public void deleteCustomer(Long memberId) {
        Map<String, Object> member = customerInfoMapper.selectMemberById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        String phone = (String) member.get("phone");

        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getPhone, phone);
        wrapper.last("LIMIT 1");
        CustomerInfo existing = customerInfoMapper.selectOne(wrapper);
        if (existing == null) {
            throw new BusinessException("该会员在后台无对应记录，无需删除");
        }
        customerInfoMapper.deleteById(existing.getId());
    }

    /**
     * 切换租客状态（同步更新 customer_info.status 和 member.status）。
     * 禁用/启用会同时影响后台管理系统和 C端登录。
     */
    @Override
    public void toggleStatus(Long memberId, Integer status) {
        Map<String, Object> member = customerInfoMapper.selectMemberById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        String phone = (String) member.get("phone");

        // 1. 更新/插入 customer_info 表
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getPhone, phone);
        wrapper.last("LIMIT 1");
        CustomerInfo existing = customerInfoMapper.selectOne(wrapper);

        if (existing == null) {
            String name = (String) member.get("realName");
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
            newRecord.setRealNameStatus(0);
            newRecord.setStatus(status);
            newRecord.setCreatedAt(LocalDateTime.now());
            customerInfoMapper.insert(newRecord);
        } else {
            CustomerInfo update = new CustomerInfo();
            update.setId(existing.getId());
            update.setStatus(status);
            update.setUpdatedAt(LocalDateTime.now());
            customerInfoMapper.updateById(update);
        }

        // 2. 同步更新 member 表的 status，C端登录时会校验此字段
        customerInfoMapper.updateMemberStatus(memberId, status);
    }

    @Override
    public IPage<CustomerOrder> getCustomerOrders(Long memberId, long pageNum, long pageSize) {
        Page<CustomerOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerOrder::getMemberId, memberId);
        wrapper.orderByDesc(CustomerOrder::getCreateTime);
        return customerOrderMapper.selectPage(page, wrapper);
    }

    /**
     * 查询租客详情。
     * 参数为 member.id，通过跨库 JOIN 查询 member + customer_info 完整信息。
     */
    @Override
    public CustomerInfo getById(Long memberId) {
        CustomerInfo customer = customerInfoMapper.selectDetailByMemberId(memberId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    /**
     * 实名认证：检查 member 表的身份证信息是否齐全（身份证号 + 正反面照），
     * 若齐全则自动将 customer_info.real_name_status 更新为 1（已认证）。
     * 
     * 调用时机：C端用户上传完身份证正反面照片并填写身份证号后，由 C端调用此接口。
     */
    @Override
    public void verifyRealName(Long memberId) {
        Map<String, Object> member = customerInfoMapper.selectMemberById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        // 查询 member 表的身份证信息
        Map<String, Object> idCardInfo = customerInfoMapper.selectMemberIdCardInfo(memberId);
        if (idCardInfo == null) {
            throw new BusinessException("无法获取会员身份证信息");
        }

        String idCard = (String) idCardInfo.get("id_card");
        String idCardFrontImg = (String) idCardInfo.get("id_card_front_img");
        String idCardBackImg = (String) idCardInfo.get("id_card_back_img");

        // 判断身份证信息是否齐全：身份证号 + 正面照 + 反面照 都必须存在且非空
        boolean isComplete = idCard != null && !idCard.isBlank()
                && idCardFrontImg != null && !idCardFrontImg.isBlank()
                && idCardBackImg != null && !idCardBackImg.isBlank();

        if (!isComplete) {
            throw new BusinessException("身份证信息不完整，需上传身份证正反面照片并填写身份证号");
        }

        // 更新 customer_info 的实名状态
        String phone = (String) member.get("phone");
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getPhone, phone);
        wrapper.last("LIMIT 1");
        CustomerInfo existing = customerInfoMapper.selectOne(wrapper);

        if (existing == null) {
            // 后台无记录，插入一条
            String name = (String) member.get("realName");
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
            newRecord.setRealNameStatus(1);  // 已认证
            newRecord.setStatus(1);
            newRecord.setCreatedAt(LocalDateTime.now());
            customerInfoMapper.insert(newRecord);
        } else {
            CustomerInfo update = new CustomerInfo();
            update.setId(existing.getId());
            update.setRealNameStatus(1);  // 已认证
            update.setUpdatedAt(LocalDateTime.now());
            customerInfoMapper.updateById(update);
        }
    }
}
