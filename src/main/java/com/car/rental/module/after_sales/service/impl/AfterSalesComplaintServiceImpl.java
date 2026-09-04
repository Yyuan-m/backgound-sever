package com.car.rental.module.after_sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.after_sales.service.AfterSalesComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterSalesComplaintServiceImpl implements AfterSalesComplaintService {

    private static final com.fasterxml.jackson.databind.ObjectMapper JACKSON = new com.fasterxml.jackson.databind.ObjectMapper();

    private final AfterSalesComplaintMapper complaintMapper;
    private final SecurityUtil securityUtil;
    private final SysUserMapper sysUserMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final CarInfoMapper carInfoMapper;

    @Override
    public IPage<AfterSalesComplaint> getPageList(long pageNum, long pageSize, String ticketNo, String type, String status, String priority) {
        Page<AfterSalesComplaint> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AfterSalesComplaint> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(ticketNo)) {
            wrapper.like(AfterSalesComplaint::getTicketNo, ticketNo);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(AfterSalesComplaint::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(AfterSalesComplaint::getStatus, status);
        }
        if (StringUtils.hasText(priority)) {
            wrapper.eq(AfterSalesComplaint::getPriority, priority);
        }
        wrapper.orderByDesc(AfterSalesComplaint::getCreatedAt);
        IPage<AfterSalesComplaint> result = complaintMapper.selectPage(page, wrapper);
        if (result != null && result.getRecords() != null && !result.getRecords().isEmpty()) {
            fillVehicleSummary(result.getRecords());
        }
        return result;
    }

    /**
     * 批量回填每张售后工单的关联车辆摘要（订单→车辆），避免逐条查询
     */
    private void fillVehicleSummary(List<AfterSalesComplaint> complaints) {
        Set<Long> orderIds = complaints.stream()
                .map(AfterSalesComplaint::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (orderIds.isEmpty()) return;

        Map<Long, CustomerOrder> orderMap = customerOrderMapper.selectBatchIds(new ArrayList<>(orderIds)).stream()
                .collect(Collectors.toMap(CustomerOrder::getId, Function.identity()));
        if (orderMap.isEmpty()) return;

        Set<Long> carIds = orderMap.values().stream()
                .map(CustomerOrder::getCarId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, CarInfo> carMap = carIds.isEmpty() ? java.util.Collections.emptyMap()
                : carInfoMapper.selectBatchIds(new ArrayList<>(carIds)).stream()
                        .collect(Collectors.toMap(CarInfo::getId, Function.identity()));

        for (AfterSalesComplaint c : complaints) {
            if (c.getOrderId() == null) continue;
            CustomerOrder order = orderMap.get(c.getOrderId());
            if (order == null) continue;
            c.setVehicleId(order.getCarId());
            c.setVehicleName(order.getCarName());
            c.setVehicleCover(order.getCarCover());
            if (order.getCarId() != null) {
                CarInfo car = carMap.get(order.getCarId());
                if (car != null) {
                    c.setPlateNumber(car.getPlateNumber());
                    if (!StringUtils.hasText(c.getVehicleName())) {
                        c.setVehicleName(car.getName());
                    }
                    if (!StringUtils.hasText(c.getVehicleCover())) {
                        c.setVehicleCover(extractFirstImage(car.getImages()));
                    }
                }
            }
        }
    }

    /**
     * 从车辆 images 字段（JSON数组字符串）提取第一张图 URL 作为封面
     */
    private String extractFirstImage(String images) {
        if (!StringUtils.hasText(images)) return null;
        String trimmed = images.trim();
        if (trimmed.startsWith("[")) {
            try {
                var arr = JACKSON.readTree(trimmed);
                if (arr.isArray() && arr.size() > 0) {
                    return arr.get(0).asText();
                }
            } catch (Exception e) {
                // 非 JSON 直接按逗号分隔
            }
        }
        int comma = trimmed.indexOf(',');
        return comma >= 0 ? trimmed.substring(0, comma).trim() : trimmed;
    }

    @Override
    public AfterSalesComplaint getById(Long id) {
        AfterSalesComplaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        return complaint;
    }

    @Override
    @Transactional
    public void addComplaint(AfterSalesComplaint complaint) {
        complaint.setCreatedAt(LocalDateTime.now());
        complaintMapper.insert(complaint);
    }

    @Override
    @Transactional
    public void updateComplaint(AfterSalesComplaint complaint) {
        AfterSalesComplaint existing = complaintMapper.selectById(complaint.getId());
        if (existing == null) {
            throw new BusinessException("售后工单不存在");
        }
        existing.setTicketNo(complaint.getTicketNo());
        existing.setOrderId(complaint.getOrderId());
        existing.setOrderNo(complaint.getOrderNo());
        existing.setCustomerName(complaint.getCustomerName());
        existing.setType(complaint.getType());
        existing.setTypeName(complaint.getTypeName());
        existing.setDescription(complaint.getDescription());
        existing.setPriority(complaint.getPriority());
        if (complaint.getStatus() != null) {
            existing.setStatus(complaint.getStatus());
        }
        complaintMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteComplaint(Long id) {
        AfterSalesComplaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        complaintMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void handleComplaint(Long id, String status, String assignee, String solution, Integer satisfaction) {
        AfterSalesComplaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        // “处理人”记录当前登录操作人的昵称（优先）/用户名，不能信任前端回传的旧值
        String handlerName = resolveHandlerName(securityUtil.getCurrentUserId());
        if (StringUtils.hasText(handlerName)) {
            assignee = handlerName;
        }
        complaintMapper.updateStatus(id, status, assignee, solution, satisfaction);
    }

    @Override
    @Transactional
    public void startProcessing(Long id) {
        AfterSalesComplaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        // 已终态（已解决/已驳回）的工单不允许再进入处理中
        if ("resolved".equals(complaint.getStatus()) || "rejected".equals(complaint.getStatus())) {
            throw new BusinessException("该工单已结束，无法开始处理");
        }
        String handlerName = resolveHandlerName(securityUtil.getCurrentUserId());
        complaintMapper.startProcessing(id, StringUtils.hasText(handlerName) ? handlerName : null);
    }

    @Override
    @Transactional
    public void updatePriority(Long id, String priority) {
        if (!StringUtils.hasText(priority)) {
            throw new BusinessException("优先级不能为空");
        }
        AfterSalesComplaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        complaintMapper.updatePriority(id, priority);
    }

    /**
     * 根据当前操作人解析处理人显示名：优先返回昵称（中文名），其次登录账号
     */
    private String resolveHandlerName(Long userId) {
        if (userId == null) return null;
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
            }
        } catch (Exception e) {
            log.warn("解析处理人名称失败, userId={}, err={}", userId, e.getMessage());
        }
        return null;
    }

    @Override
    public CarInfo getVehicleByComplaintId(Long complaintId) {
        AfterSalesComplaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new BusinessException("售后工单不存在");
        }
        if (complaint.getOrderId() == null) {
            return null;
        }
        CustomerOrder order = customerOrderMapper.selectById(complaint.getOrderId());
        if (order == null || order.getCarId() == null) {
            return null;
        }
        return carInfoMapper.selectById(order.getCarId());
    }
}