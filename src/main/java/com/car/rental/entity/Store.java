package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门店配置实体（前台官网查询取车门店）
 */
@Data
@TableName("customer_store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属城市ID */
    private Long cityId;

    /** 门店名称 */
    private String name;

    /** 门店地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 排序（越小越靠前） */
    private Integer sort;

    /** 状态：1=启用，0=禁用 */
    private Integer status;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
