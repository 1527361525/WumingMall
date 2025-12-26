package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private String productId;

    private String name;

    private BigDecimal price;

    private String description;

    private String productImage;

    /**
     * 销量
     */
    private Integer totalSales;
}
