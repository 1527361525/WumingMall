package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {
    private Long productId;

    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    private String description;

    private String productImage;
}
