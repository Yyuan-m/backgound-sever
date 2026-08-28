package com.car.rental.module.customer.dto;

import lombok.Data;

/**
 * 实名认证审核 DTO
 */
@Data
public class VerifyReviewDTO {

    /** true=通过 false=驳回 */
    private Boolean approved;

    /** 驳回原因（驳回时必填） */
    private String rejectReason;
}
