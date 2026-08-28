package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员实名认证记录（实际存储在 car_rental_customer.member_verify_record，跨库访问）
 */
@Data
@TableName("car_rental_customer.member_verify_record")
public class MemberVerifyRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private String realName;
    private String idCard;
    private LocalDate birthDate;
    private String driverLicenseNo;
    private String driverLicenseType;
    private LocalDate driverLicenseExpireDate;
    private String idCardFrontImg;
    private String idCardBackImg;
    private String driverLicenseFrontImg;
    private String driverLicenseBackImg;

    /** pending待审核/approved已通过/rejected已驳回 */
    private String status;
    private String rejectReason;
    private String reviewer;
    private LocalDateTime reviewTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;

    // ---------- 冗余展示字段（JOIN member 表填充） ----------

    /** 会员账号（member.username） */
    @TableField(exist = false)
    private String username;

    /** 会员昵称 */
    @TableField(exist = false)
    private String nickname;

    /** 会员手机号 */
    @TableField(exist = false)
    private String phone;

    /** 会员头像 */
    @TableField(exist = false)
    private String avatar;

    /** 会员当前认证状态（member.verify_status） */
    @TableField(exist = false)
    private String memberVerifyStatus;
}
