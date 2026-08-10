package com.car.rental.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CarMaintenance {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private String type;
    private String description;
    private BigDecimal cost;
    private Integer mileage;
    private String company;
    private String status;
    private LocalDate maintenanceDate;
    private LocalDate nextDate;
    private String remark;
    private LocalDateTime createdAt;
}