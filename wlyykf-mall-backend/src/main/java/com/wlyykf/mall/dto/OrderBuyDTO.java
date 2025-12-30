package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderBuyDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    @NotBlank
    private String address;

    private Long userId;
}
