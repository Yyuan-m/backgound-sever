package com.car.rental.module.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.CustomerInfo;
import com.car.rental.entity.CustomerOrder;
import com.car.rental.module.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客户管理", description = "客户信息增删改查、状态启停、客户订单查询与实名认证")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "客户列表（分页）", description = "支持关键字、状态、会员等级筛选。需要 customer:list 权限")
    @GetMapping("/list")
    @RequirePermission("customer:list")
    public Result<PageResult<CustomerInfo>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：姓名/手机号模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "客户状态筛选") @RequestParam(required = false) Integer status,
            @Parameter(description = "会员等级筛选") @RequestParam(required = false) String level) {
        IPage<CustomerInfo> page = customerService.getCustomerList(pageNum, pageSize, keyword, status, level);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "客户详情", description = "按客户ID查询客户详细信息。需要 customer:list 权限")
    @GetMapping("/detail/{id}")
    @RequirePermission("customer:list")
    public Result<CustomerInfo> detail(@Parameter(description = "客户ID") @PathVariable Long id) {
        return Result.ok(customerService.getById(id));
    }

    @Operation(summary = "编辑客户", description = "编辑客户信息。需要 customer:update 权限。注意：此接口的 id 参数为 member.id（非 customer_info.id），Service 内部通过 member.id → phone → customer_info upsert 实现，故无法直接用 @LogChanges 的 selectById 查旧数据（会查到错误记录）")
    @PutMapping("/update/{id}")
    @RequirePermission("customer:update")
    // 注意：此接口的 id 参数为 car_rental_customer.member.id（非 customer_info.id），
    // Service 内部通过 member.id → phone → customer_info upsert 实现，
    // 故无法直接用 @LogChanges 的 selectById 查旧数据（会查到错误记录）。
    public Result<Void> update(@Parameter(description = "会员ID（member.id，非 customer_info.id）") @PathVariable Long id, @RequestBody CustomerInfo customer) {
        customerService.updateCustomer(id, customer);
        return Result.ok();
    }

    @Operation(summary = "删除客户", description = "软删除客户信息。需要 customer:delete 权限。同 update：id 为 member.id，Service 内部做 upsert/软删除 customer_info，不使用 @LogChanges 避免日志记录错误的旧数据快照")
    @DeleteMapping("/delete/{id}")
    @RequirePermission("customer:delete")
    // 同 update：id 为 member.id，Service 内部做 upsert/软删除 customer_info，
    // 不使用 @LogChanges 避免日志记录错误的旧数据快照。
    public Result<Void> delete(@Parameter(description = "会员ID（member.id，非 customer_info.id）") @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.ok();
    }

    @Operation(summary = "启用/停用客户", description = "切换客户启用停用状态。需要 customer:status 权限")
    @PutMapping("/status/{id}")
    @RequirePermission("customer:status")
    public Result<Void> status(@Parameter(description = "客户ID") @PathVariable Long id,
                               @Parameter(description = "目标状态") @RequestParam Integer status) {
        customerService.toggleStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "客户订单（分页）", description = "查指定客户的历史订单列表，分页返回。需要 customer:orders 权限")
    @GetMapping("/orders/{id}")
    @RequirePermission("customer:orders")
    public Result<PageResult<CustomerOrder>> orders(
            @Parameter(description = "客户ID") @PathVariable Long id,
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<CustomerOrder> page = customerService.getCustomerOrders(id, pageNum, pageSize);
        return Result.ok(PageResult.of(page));
    }

    /**
     * 实名认证：C端用户上传完身份证正反面照片并填写身份证号后调用。
     * 后端自动检查 member 表的身份证信息是否齐全，若齐全则更新 real_name_status = 1。
     */
    @Operation(summary = "实名认证", description = "C端用户上传完身份证正反面照片并填写身份证号后调用，后端自动检查 member 表的身份证信息是否齐全，若齐全则更新 real_name_status = 1")
    @PutMapping("/real-name-status/{id}")
    public Result<Void> updateRealNameStatus(@Parameter(description = "会员ID（member.id）") @PathVariable Long id) {
        customerService.verifyRealName(id);
        return Result.ok();
    }
}