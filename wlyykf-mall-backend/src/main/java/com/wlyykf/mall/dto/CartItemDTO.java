package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CartItemDTO {
    private Long cartItemId;

    private Long productId;

    private Integer quantity;

    private Integer selected;
}
