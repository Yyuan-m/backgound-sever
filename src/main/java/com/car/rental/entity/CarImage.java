package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("car_image")
public class CarImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long vehicleId;

    private String vehicleName;

    private String category;

    private String url;

    private Integer status;

    private LocalDateTime createdAt;
}
