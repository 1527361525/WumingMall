package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateVO {
    private String productId;

    private String name;

    private String categoryId;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private String productImage;

    private String categoryName;
}
