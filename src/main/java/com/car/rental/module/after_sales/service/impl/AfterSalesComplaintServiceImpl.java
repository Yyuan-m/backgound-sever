package com.car.rental.module.after_sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.after_sales.service.AfterSalesComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterSalesComplaintServiceImpl implements AfterSalesComplaintService {

    private final AfterSalesComplaintMapper complaintMapper;

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
        return complaintMapper.selectPage(page, wrapper);
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
        complaintMapper.updateStatus(id, status, assignee, solution, satisfaction);
    }
}