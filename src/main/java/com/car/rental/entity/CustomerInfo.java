package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("customer_info")
public class CustomerInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private String avatar;

    private String idCard;

    private Integer realNameStatus;

    private String driverLicense;

    private LocalDate driverLicenseExpire;

    private String membershipLevel;

    private String membershipName;

    private Integer creditScore;

    private Integer totalOrders;

    private BigDecimal totalSpent;

    private BigDecimal discount;

    @TableField(value = "tags")
    private String tags;

    private Integer isBlacklist;

    private Integer status;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ============ 扩展字段（来自 car_rental_customer.member 表，跨库 JOIN 查询） ============
    // 这些字段不持久化到 customer_info 表，仅在查询时通过跨库 LEFT JOIN 填充

    /** 关联的官网会员ID（car_rental_customer.member.id） */
    @TableField(exist = false)
    private Long memberId;

    /** customer_info 表的主键ID（与 member 通过 phone 关联；可能为 null 表示后台尚无对应记录） */
    @TableField(exist = false)
    private Long customerId;

    /** 官网账号用户名 */
    @TableField(exist = false)
    private String username;

    /** 昵称 */
    @TableField(exist = false)
    private String nickname;

    /** 真实姓名（与 name 字段含义相同，但来源不同：name 是后台维护，realName 来自官网注册） */
    @TableField(exist = false)
    private String realName;

    /** 性别：0未知 1男 2女 */
    @TableField(exist = false)
    private Integer gender;

    /** 生日 */
    @TableField(exist = false)
    private LocalDate birthday;

    /** 邮箱 */
    @TableField(exist = false)
    private String email;

    /** 身份证正面照URL */
    @TableField(exist = false)
    private String idCardFrontImg;

    /** 身份证背面照URL */
    @TableField(exist = false)
    private String idCardBackImg;

    /** 驾驶证准驾车型（C1/C2/B1/B2/A1/A2/A3） */
    @TableField(exist = false)
    private String driverLicenseType;

    /** 驾驶证正面照URL */
    @TableField(exist = false)
    private String driverLicenseFrontImg;

    /** 驾驶证背面照URL */
    @TableField(exist = false)
    private String driverLicenseBackImg;

    /** 省份 */
    @TableField(exist = false)
    private String province;

    /** 城市 */
    @TableField(exist = false)
    private String city;

    /** 详细地址 */
    @TableField(exist = false)
    private String address;

    /** 最后登录时间 */
    @TableField(exist = false)
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    @TableField(exist = false)
    private String lastLoginIp;
}
