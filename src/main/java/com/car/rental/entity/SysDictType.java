package com.car.rental.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysDictType {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}