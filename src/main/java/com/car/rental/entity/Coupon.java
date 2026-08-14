package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券主表（券模板）
 * status 持久化运营状态：draft 草稿 / published 已投放 / offline 已下线
 * 业务状态（查询时动态计算，不持久化）：
 *   - draft        草稿（运营未投放）
 *   - pending      待生效（已投放但 valid_start_time > now）
 *   - published    已投放（正常可用）
 *   - sold_out     已领完（received_count >= total_count，且 total_count != -1）
 *   - expired      已过期（valid_end_time < now）
 *   - offline      已下线（运营主动下线）
 * published=0 未确认投放 / 1 已确认投放（防止运营误发）
 */
@Data
@TableName("coupon")
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 券码（全局唯一，C端识别/核销用） */
    private String code;

    private String name;

    /** discount折扣/deduction满减/duration时长 */
    private String type;

    private String typeName;

    /** 优惠值（折扣填0.88/满减填金额/时长填天数） */
    private BigDecimal value;

    /** 最低消费门槛 */
    private BigDecimal minAmount;

    /** 折扣券封顶优惠金额（仅 discount 有效，NULL=不封顶） */
    private BigDecimal discountCap;

    /** 发放总量（-1表示无限） */
    private Integer totalCount;

    /** 已领取数量 */
    private Integer receivedCount;

    /** 已核销数量 */
    private Integer usedCount;

    /** 每人限领张数 */
    private Integer perUserLimit;

    /** 适用范围 all全场通用/specified指定车辆 */
    private String applyScope;

    /** 是否可叠加使用 0不可叠加/1可叠加 */
    private Integer stackable;

    private LocalDateTime validStartTime;

    private LocalDateTime validEndTime;

    /** draft草稿/published已投放/offline已下线 */
    private String status;

    /** 确认投放标志 0未投放/1已投放 */
    private Integer published;

    private LocalDateTime publishedAt;

    private Long publishedBy;

    private String remark;

    private Long createdBy;

    private LocalDateTime createdAt;

    private Long updatedBy;

    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @Version
    private Integer version;

    // ============ 非持久化扩展字段（查询时填充） ============

    /** 关联车辆ID列表（apply_scope=specified 时使用，新增/编辑时传入） */
    @TableField(exist = false)
    private java.util.List<Long> carIds;

    /** 关联车辆名称列表（查询展示用） */
    @TableField(exist = false)
    private java.util.List<String> carNames;
}
