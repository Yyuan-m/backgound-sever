package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 城市配置实体（前台官网查询取车城市）
 */
@Data
@TableName("customer_city")
public class City {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 城市名称 */
    private String name;

    /** 排序（越小越靠前） */
    private Integer sort;

    /** 状态：1=启用，0=禁用 */
    private Integer status;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
