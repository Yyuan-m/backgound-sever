package com.car.rental.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysDictData {
    private Long id;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer sortOrder;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}