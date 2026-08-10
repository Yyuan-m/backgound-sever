package com.car.rental.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long pageNum = 1;
    private long pageSize = 10;
}