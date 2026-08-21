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
@TableName("car_info")
public class CarInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String brand;

    private String series;

    private String type;

    private String plateNumber;

    private String vin;

    private String engineNo;

    private LocalDate registrationDate;

    private Integer mileage;

    private String displacement;

    private Integer seats;

    private String color;

    private String conditionLevel;

    private BigDecimal originalValue;

    private BigDecimal residualValue;

    private String status;

    private BigDecimal dailyPrice;

    /** 日成本价（默认=日租金×0.54，可手动覆盖） */
    private BigDecimal dailyCost;

    /** 起租天数（最小租期，1-180，默认1） */
    private Integer minRentDays;

    /** 最大租车天数（NULL=不限制） */
    private Integer maxRentDays;

    private BigDecimal halfDayPrice;

    private BigDecimal nightPrice;

    private BigDecimal weeklyDiscount;

    private BigDecimal monthlyDiscount;

    private BigDecimal holidaySurcharge;

    private BigDecimal overtimePerHour;

    private BigDecimal remoteReturnFee;

    @TableField(value = "images")
    private String images;

    private String description;

    @TableField(value = "tags")
    private String tags;

    private Integer isHot;

    private Integer isRecommend;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 车辆配置（非数据库字段）：
     * - 详情接口返回时携带关联的 car_config 记录
     * - 新增/更新车辆时，前端可一并传入配置数据
     */
    @TableField(exist = false)
    private CarConfig carConfig;

    /**
     * 关联素材列表（非数据库字段）：
     * - 详情接口返回时携带该车辆的所有素材（car_image 记录）
     * - 列表接口不填充，仅详情接口填充
     */
    @TableField(exist = false)
    private java.util.List<CarImage> materials;

    /**
     * 素材数量（非数据库字段）：
     * - 列表接口返回时携带该车辆的素材总数，便于列表展示
     */
    @TableField(exist = false)
    private Integer materialCount;
}