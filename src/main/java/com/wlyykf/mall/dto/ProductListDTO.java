package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductListDTO {

    private Long categoryId;

    @NotNull
    private Integer orderType;

    @NotNull
    private Integer sortDirection;

    private Integer pageNum;

    private Integer pageSize;
}
