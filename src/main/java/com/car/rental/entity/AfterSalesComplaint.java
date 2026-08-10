package com.car.rental.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AfterSalesComplaint {
    private Long id;
    private String ticketNo;
    private Long orderId;
    private String orderNo;
    private String customerName;
    private String type;
    private String typeName;
    private String description;
    private String priority;
    private String status;
    private String assignee;
    private String solution;
    private Integer satisfaction;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}