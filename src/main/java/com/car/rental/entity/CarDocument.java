package com.car.rental.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CarDocument {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private String docType;
    private String docNumber;
    private LocalDate issueDate;
    private LocalDate expireDate;
    private String status;
    private String imageUrl;
    private String remark;
    private LocalDateTime createdAt;
}