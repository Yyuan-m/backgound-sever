package com.car.rental.module.order.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.entity.CarInfo;
import com.car.rental.entity.FinanceRecord;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.marketing.service.CustomerCouponService;
import com.car.rental.module.order.service.OrderService;
import com.car.rental.entity.Invoice;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerOrderMapper customerOrderMapper;
    private final CustomerCouponService customerCouponService;
    private final InvoiceMapper invoiceMapper;
    private final FinanceRecordMapper financeRecordMapper;
    private final CarInfoMapper carInfoMapper;

    @Override
    public IPage<CustomerOrder> getOrderList(long pageNum, long pageSize, String keyword, String status, String startDate, String endDate) {
        Page<CustomerOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(CustomerOrder::getOrderNo, keyword)
                    .or().like(CustomerOrder::getContactName, keyword)
                    .or().like(CustomerOrder::getContactPhone, keyword)
                    .or().like(CustomerOrder::getCarName, keyword));
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(CustomerOrder::getStatus, status);
        }
        if (StrUtil.isNotBlank(startDate)) {
            LocalDateTime start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            wrapper.ge(CustomerOrder::getCreateTime, start);
        }
        if (StrUtil.isNotBlank(endDate)) {
            LocalDateTime end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay();
            wrapper.le(CustomerOrder::getCreateTime, end);
        }
        wrapper.orderByDesc(CustomerOrder::getCreateTime);
        return customerOrderMapper.selectPage(page, wrapper);
    }

    /**
     * 按状态统计订单数量（全量，不受分页/筛选条件影响）
     * 使用原生 SQL 避免 MyBatis-Plus QueryWrapper 在 selectMaps + 聚合函数场景下
     * 的解析差异（特别是 @TableLogic 是否自动追加 is_delete 条件的行为不确定）
     */
    @Override
    public Map<String, Long> getStatusCount() {
        List<Map<String, Object>> rows = customerOrderMapper.selectStatusCount();
        Map<String, Long> result = new HashMap<>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Object statusVal = row.get("status");
            Object cntVal = row.get("cnt");
            if (statusVal == null || cntVal == null) continue;
            try {
                result.put(String.valueOf(statusVal), ((Number) cntVal).longValue());
            } catch (ClassCastException e) {
                log.warn("订单状态统计值类型转换失败: status={}, cnt={}", statusVal, cntVal);
            }
        }
        return result;
    }

    @Override
    public CustomerOrder getOrderDetail(Long id) {
        CustomerOrder order = customerOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /**
     * 创建订单
     * 优惠券由 C 端下单流程预先 lock（调 /api/customer/coupon/lock），
     * 此处仅保存订单并回写 couponUserId/couponId/couponDiscount（由 C 端通过 calculate 算好后传入）
     */
    @Override
    @Transactional
    public void addOrder(CustomerOrder order) {
        if (StrUtil.isBlank(order.getOrderNo())) {
            order.setOrderNo(generateOrderNo());
        }
        if (StrUtil.isBlank(order.getStatus())) {
            order.setStatus("pending");
            order.setStatusName("待支付");
        }
        if (order.getCreateTime() == null) {
            order.setCreateTime(LocalDateTime.now());
        }
        customerOrderMapper.insert(order);
    }

    /**
     * 更新订单状态，并联动处理优惠券：
     * cancelled → 释放锁定的券（cancelLock，幂等，失败不阻断订单取消）
     * completed → 核销券（verify，幂等，失败则回滚整个事务）
     */
    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        CustomerOrder existing = customerOrderMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }
        String oldStatus = existing.getStatus();

        existing.setStatus(status);
        existing.setStatusName(resolveStatusName(status));
        customerOrderMapper.updateById(existing);

        // 优惠券联动（仅当订单使用了券）
        Long couponUserId = existing.getCouponUserId();
        Long memberId = existing.getMemberId();
        if (couponUserId != null && memberId != null) {
            if ("cancelled".equals(status) && !"cancelled".equals(oldStatus)) {
                // 订单取消：释放锁定的券，失败仅记录日志（避免阻断取消流程）
                try {
                    customerCouponService.cancelLock(couponUserId, memberId);
                } catch (Exception e) {
                    log.warn("订单取消时释放优惠券失败 orderId={}, couponUserId={}: {}", id, couponUserId, e.getMessage());
                }
            } else if ("completed".equals(status) && !"completed".equals(oldStatus)) {
                // 订单完成：核销券（verify 已幂等，失败则整个事务回滚，保证一致性）
                customerCouponService.verify(couponUserId, memberId, id);
            }
        }

        // 订单完成时自动生成发票 + 财务流水（收入+支出，幂等：已存在则跳过）
        if ("completed".equals(status) && !"completed".equals(oldStatus)) {
            autoGenerateInvoice(existing);
            autoGenerateFinanceRecords(existing);
        }
    }

    /**
     * 自动生成财务流水（订单完成时调用）
     * 1. rental 收入流水：amount = total_amount（实付净额，已扣除优惠券折扣）
     *    - 净收入 = rent_amount - coupon_discount = total_amount
     *    - 若 total_amount 为空则回退使用 rent_amount
     * 2. rental_cost 支出流水：amount = daily_cost × days（车辆租赁成本）
     * 幂等设计：按 order_no + type 查询，已存在则跳过
     */
    private void autoGenerateFinanceRecords(CustomerOrder order) {
        LocalDateTime now = LocalDateTime.now();
        // 1. 收入流水（rental）—— 使用实付净额（totalAmount），扣除优惠券
        LambdaQueryWrapper<FinanceRecord> rentalWrapper = new LambdaQueryWrapper<>();
        rentalWrapper.eq(FinanceRecord::getOrderNo, order.getOrderNo())
                .eq(FinanceRecord::getType, "rental");
        if (financeRecordMapper.selectCount(rentalWrapper) == 0) {
            BigDecimal netAmount = order.getTotalAmount() != null
                    ? order.getTotalAmount()
                    : order.getRentAmount();
            FinanceRecord income = new FinanceRecord();
            income.setType("rental");
            income.setTypeName("租金收入");
            income.setOrderNo(order.getOrderNo());
            income.setCustomerName(order.getContactName());
            income.setAmount(netAmount);
            income.setMethod("系统自动");
            income.setStatus("completed");
            income.setCreatedAt(now);
            income.setUpdatedAt(now);
            financeRecordMapper.insert(income);
        }
        // 2. 支出流水（rental_cost = daily_cost × days）
        if (order.getCarId() != null) {
            CarInfo car = carInfoMapper.selectById(order.getCarId());
            if (car != null && car.getDailyCost() != null && order.getDays() != null) {
                LambdaQueryWrapper<FinanceRecord> costWrapper = new LambdaQueryWrapper<>();
                costWrapper.eq(FinanceRecord::getOrderNo, order.getOrderNo())
                        .eq(FinanceRecord::getType, "rental_cost");
                if (financeRecordMapper.selectCount(costWrapper) == 0) {
                    BigDecimal costAmount = car.getDailyCost()
                            .multiply(BigDecimal.valueOf(order.getDays()));
                    FinanceRecord cost = new FinanceRecord();
                    cost.setType("rental_cost");
                    cost.setTypeName("车辆租赁成本");
                    cost.setOrderNo(order.getOrderNo());
                    cost.setCustomerName(order.getContactName());
                    cost.setAmount(costAmount);
                    cost.setMethod("系统自动");
                    cost.setStatus("completed");
                    cost.setCreatedAt(now);
                    cost.setUpdatedAt(now);
                    financeRecordMapper.insert(cost);
                }
            }
        }
    }

    /**
     * 自动生成发票：订单完成时创建 pending 状态发票，金额=rent_amount
     * 幂等设计：同一 order_no 已有发票则跳过
     */
    private void autoGenerateInvoice(CustomerOrder order) {
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Invoice::getOrderNo, order.getOrderNo());
        if (invoiceMapper.selectCount(wrapper) > 0) {
            return; // 已有发票，跳过
        }
        Invoice invoice = new Invoice();
        invoice.setOrderNo(order.getOrderNo());
        invoice.setCustomerName(order.getContactName());
        invoice.setAmount(order.getRentAmount());
        invoice.setType("增值税普通发票");
        invoice.setStatus("pending");
        invoiceMapper.insert(invoice);
    }

    private String resolveStatusName(String status) {
        switch (status) {
            case "pending":   return "待支付";
            case "renting":    return "租赁中";
            case "completed":  return "已完成";
            case "cancelled":  return "已取消";
            default:           return status;
        }
    }

    private String generateOrderNo() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "LC" + datePrefix;
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(CustomerOrder::getOrderNo, prefix);
        wrapper.orderByDesc(CustomerOrder::getOrderNo);
        wrapper.last("LIMIT 1");
        CustomerOrder lastOrder = customerOrderMapper.selectOne(wrapper);
        int seq = 1;
        if (lastOrder != null && lastOrder.getOrderNo().length() >= prefix.length() + 3) {
            String seqStr = lastOrder.getOrderNo().substring(prefix.length());
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        if (seq > 999) {
            seq = 1;
        }
        return prefix + String.format("%03d", seq);
    }

    @Override
    public void updateOrder(Long id, CustomerOrder order) {
        CustomerOrder existing = customerOrderMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }
        existing.setCarId(order.getCarId());
        existing.setCarName(order.getCarName());
        existing.setCarCover(order.getCarCover());
        existing.setStartDate(order.getStartDate());
        existing.setEndDate(order.getEndDate());
        existing.setDays(order.getDays());
        existing.setDailyPrice(order.getDailyPrice());
        existing.setRentAmount(order.getRentAmount());
        existing.setCouponDiscount(order.getCouponDiscount());
        existing.setCouponId(order.getCouponId());
        existing.setCouponUserId(order.getCouponUserId());
        existing.setTotalAmount(order.getTotalAmount());
        existing.setCity(order.getCity());
        existing.setStore(order.getStore());
        existing.setContactName(order.getContactName());
        existing.setContactPhone(order.getContactPhone());
        if (StrUtil.isNotBlank(order.getStatus())) {
            existing.setStatus(order.getStatus());
        }
        if (StrUtil.isNotBlank(order.getStatusName())) {
            existing.setStatusName(order.getStatusName());
        }
        customerOrderMapper.updateById(existing);
    }

    @Override
    public void deleteOrder(Long id) {
        CustomerOrder existing = customerOrderMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }
        customerOrderMapper.deleteById(id);
    }

    /**
     * 自动完成到期订单：renting 且 end_date < 今天 → completed
     * 走 updateOrderStatus 流程，确保发票 + 财务流水一并生成。
     * end_date 当天仍视为租赁中（与 C 端 autoCompleteOrders 一致），次日才自动完成。
     * 单条失败不阻断其他订单；优惠券核销已幂等（C 端支付时已核销，重复调用安全）。
     */
    @Override
    public int autoCompleteExpiredOrders() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerOrder::getStatus, "renting")
                .lt(CustomerOrder::getEndDate, today);
        List<CustomerOrder> expired = customerOrderMapper.selectList(wrapper);
        if (expired.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (CustomerOrder order : expired) {
            try {
                updateOrderStatus(order.getId(), "completed");
                count++;
                log.info("订单到期自动完成（管理后台）: orderNo={}, endDate={}",
                        order.getOrderNo(), order.getEndDate());
            } catch (Exception e) {
                log.error("订单到期自动完成失败 orderNo={}: {}", order.getOrderNo(), e.getMessage(), e);
            }
        }
        return count;
    }

    /**
     * 回补缺失财务流水 + 发票：扫描 completed 订单中缺少 rental 类型流水的，
     * 直接补生成（不经过 updateOrderStatus，避免重复触发优惠券核销等副作用）。
     * 幂等：autoGenerateInvoice / autoGenerateFinanceRecords 内部已按 order_no 去重。
     */
    @Override
    public int backfillMissingFinanceRecords() {
        // 查询所有 completed 订单
        LambdaQueryWrapper<CustomerOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(CustomerOrder::getStatus, "completed");
        List<CustomerOrder> completedOrders = customerOrderMapper.selectList(orderWrapper);
        if (completedOrders.isEmpty()) {
            return 0;
        }

        // 查询所有已有 rental 流水的 order_no（用于判定是否需要回补）
        LambdaQueryWrapper<FinanceRecord> frWrapper = new LambdaQueryWrapper<>();
        frWrapper.eq(FinanceRecord::getType, "rental")
                .select(FinanceRecord::getOrderNo);
        List<FinanceRecord> existing = financeRecordMapper.selectList(frWrapper);
        Set<String> orderNoWithRental = existing.stream()
                .map(FinanceRecord::getOrderNo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int count = 0;
        for (CustomerOrder order : completedOrders) {
            if (orderNoWithRental.contains(order.getOrderNo())) {
                continue; // 已有收入流水，跳过
            }
            try {
                autoGenerateInvoice(order);
                autoGenerateFinanceRecords(order);
                count++;
                log.info("回补财务流水: orderNo={}, totalAmount={}, rentAmount={}",
                        order.getOrderNo(), order.getTotalAmount(), order.getRentAmount());
            } catch (Exception e) {
                log.error("回补财务流水失败 orderNo={}: {}", order.getOrderNo(), e.getMessage(), e);
            }
        }
        return count;
    }
}
