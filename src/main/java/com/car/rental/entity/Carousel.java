package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Carousel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}