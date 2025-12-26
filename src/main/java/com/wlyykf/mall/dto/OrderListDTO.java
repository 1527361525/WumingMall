package com.wlyykf.mall.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderListDTO {
    private LocalDate startDate;

    private LocalDate endDate;

    private Integer orderStatus;

    private Integer pageNum;

    private Integer pageSize;

    private Long userId;
}
