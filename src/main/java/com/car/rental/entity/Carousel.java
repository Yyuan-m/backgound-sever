package com.car.rental.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Carousel {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}