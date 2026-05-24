package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 推荐商品VO
 */
@Data
public class RecommendProductVO {

    private Long productId;

    private String name;

    private String productImage;

    private BigDecimal price;

    /**
     * 购买次数（推荐权重）
     */
    private Integer buyCount;
}
