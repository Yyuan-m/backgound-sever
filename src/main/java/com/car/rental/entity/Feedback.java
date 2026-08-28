package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户预约咨询/留言反馈（实际存储在 car_rental_customer.feedback，跨库访问）
 *
 * type = appointment 预约咨询（C端预约看车意向，含意向车型/取车日期）
 *      = feedback     留言反馈
 */
@Data
@TableName("car_rental_customer.feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会员ID（登录提交时绑定，未登录为空） */
    private Long memberId;

    /** 类型：appointment预约咨询 / feedback留言反馈 */
    private String type;

    private String name;

    private String phone;

    /** 留言内容 */
    private String content;

    /** 意向车型（预约咨询） */
    private String carType;

    /** 取车日期（预约咨询） */
    private LocalDate rentDate;

    /** 状态：pending待处理 / handled已处理 */
    private String status;

    /** 处理备注（沟通结果/说明） */
    private String remark;

    /** 处理人 */
    private String handler;

    /** 最近处理时间 */
    private LocalDateTime processTime;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    // ---------- 冗余展示字段（JOIN member 表填充） ----------

    /** 会员账号（member.username） */
    @TableField(exist = false)
    private String username;

    /** 会员昵称 */
    @TableField(exist = false)
    private String nickname;

    /** 会员头像 */
    @TableField(exist = false)
    private String avatar;
}
