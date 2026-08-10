package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarViolation;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.car.mapper.CarViolationMapper;
import com.car.rental.module.car.service.CarViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarViolationServiceImpl implements CarViolationService {

    private final CarViolationMapper carViolationMapper;
    private final CustomerOrderMapper customerOrderMapper;

    @Override
    public PageResult<CarViolation> getList(Integer pageNum, Integer pageSize, Long vehicleId, Long orderId, String violationType, String status) {
        Page<CarViolation> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<CarViolation> wrapper = new LambdaQueryWrapper<>();

        if (vehicleId != null) {
            wrapper.eq(CarViolation::getVehicleId, vehicleId);
        }
        if (orderId != null) {
            wrapper.eq(CarViolation::getOrderId, orderId);
        }
        if (StringUtils.hasText(violationType)) {
            wrapper.eq(CarViolation::getViolationType, violationType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(CarViolation::getStatus, status);
        }
        wrapper.orderByDesc(CarViolation::getCreatedAt);

        IPage<CarViolation> result = carViolationMapper.selectPage(page, wrapper);
        fillOrderInfo(result.getRecords());
        return PageResult.of(result);
    }

    /**
     * 批量填充关联订单的 orderNo 和 customerName，避免 N+1 查询。
     */
    private void fillOrderInfo(List<CarViolation> violations) {
        if (violations == null || violations.isEmpty()) {
            return;
        }
        Set<Long> orderIds = violations.stream()
                .map(CarViolation::getOrderId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (orderIds.isEmpty()) {
            return;
        }
        List<CustomerOrder> orders = customerOrderMapper.selectBatchIds(orderIds);
        Map<Long, CustomerOrder> orderMap = orders.stream()
                .collect(Collectors.toMap(CustomerOrder::getId, o -> o, (a, b) -> a));
        for (CarViolation v : violations) {
            if (v.getOrderId() == null) {
                continue;
            }
            CustomerOrder o = orderMap.get(v.getOrderId());
            if (o != null) {
                v.setOrderNo(o.getOrderNo());
                v.setCustomerName(o.getContactName());
            }
        }
    }

    @Override
    public CarViolation getById(Long id) {
        CarViolation violation = carViolationMapper.selectById(id);
        if (violation == null) {
            throw new BusinessException("违章记录不存在");
        }
        fillOrderInfo(Collections.singletonList(violation));
        return violation;
    }

    @Override
    @Transactional
    public void add(CarViolation carViolation) {
        carViolationMapper.insert(carViolation);
    }

    @Override
    @Transactional
    public void update(CarViolation carViolation) {
        CarViolation existing = carViolationMapper.selectById(carViolation.getId());
        if (existing == null) {
            throw new BusinessException("违章记录不存在");
        }
        carViolationMapper.updateById(carViolation);
    }

    @Override
    @Transactional
    public void handle(Long id, String status, String handler, String handleDate) {
        CarViolation existing = carViolationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("违章记录不存在");
        }
        existing.setStatus(status);
        existing.setHandler(handler);
        // 处理日期：前端传值优先，否则取当前时间
        if (StringUtils.hasText(handleDate)) {
            try {
                existing.setHandleDate(LocalDate.parse(handleDate).atStartOfDay());
            } catch (Exception e) {
                existing.setHandleDate(LocalDateTime.now());
            }
        } else {
            existing.setHandleDate(LocalDateTime.now());
        }
        carViolationMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CarViolation existing = carViolationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("违章记录不存在");
        }
        carViolationMapper.deleteById(id);
    }
}