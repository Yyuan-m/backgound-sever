package com.car.rental.module.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.module.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/list")
    @RequirePermission("customer:list")
    public Result<PageResult<CustomerInfo>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String level) {
        IPage<CustomerInfo> page = customerService.getCustomerList(pageNum, pageSize, keyword, status, level);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/detail/{id}")
    @RequirePermission("customer:list")
    public Result<CustomerInfo> detail(@PathVariable Long id) {
        return Result.ok(customerService.getById(id));
    }

    @PutMapping("/update/{id}")
    @RequirePermission("customer:update")
    // 注意：此接口的 id 参数为 car_rental_customer.member.id（非 customer_info.id），
    // Service 内部通过 member.id → phone → customer_info upsert 实现，
    // 故无法直接用 @LogChanges 的 selectById 查旧数据（会查到错误记录）。
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerInfo customer) {
        customerService.updateCustomer(id, customer);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission("customer:delete")
    // 同 update：id 为 member.id，Service 内部做 upsert/软删除 customer_info，
    // 不使用 @LogChanges 避免日志记录错误的旧数据快照。
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    @RequirePermission("customer:status")
    public Result<Void> status(@PathVariable Long id, @RequestParam Integer status) {
        customerService.toggleStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/orders/{id}")
    @RequirePermission("customer:orders")
    public Result<PageResult<CustomerOrder>> orders(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<CustomerOrder> page = customerService.getCustomerOrders(id, pageNum, pageSize);
        return Result.ok(PageResult.of(page));
    }

    /**
     * 实名认证：C端用户上传完身份证正反面照片并填写身份证号后调用。
     * 后端自动检查 member 表的身份证信息是否齐全，若齐全则更新 real_name_status = 1。
     */
    @PutMapping("/real-name-status/{id}")
    public Result<Void> updateRealNameStatus(@PathVariable Long id) {
        customerService.verifyRealName(id);
        return Result.ok();
    }
}