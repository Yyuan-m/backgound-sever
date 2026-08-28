package com.car.rental.module.feedback.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 处理预约咨询/留言反馈的请求体
 */
@Data
public class FeedbackProcessDTO {

    /** 处理备注（沟通结果/说明），处理时必填 */
    @NotBlank(message = "处理备注不能为空")
    private String remark;
}
