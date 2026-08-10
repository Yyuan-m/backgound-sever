package com.car.rental.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GpsTrack {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer speed;
    private String address;
    private String status;
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;
}