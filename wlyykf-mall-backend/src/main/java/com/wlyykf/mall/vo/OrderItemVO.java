package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private String productId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private String productImage;
}
