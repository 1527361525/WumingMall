package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
    private String cartItemId;

    private String productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private Integer quantity;

    private Integer selected;
}
