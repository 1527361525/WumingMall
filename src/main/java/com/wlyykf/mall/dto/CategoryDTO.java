package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long categoryId;

    @NotBlank(message = "名称不能为空")
    private String name;

    private Long parentId;

    @NotNull(message = "排序不能为空")
    private Integer sort;
}
