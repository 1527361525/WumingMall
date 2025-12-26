package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderAddDTO {
    private Long orderId;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Integer orderStatus;

    @NotBlank
    private String address;
}
