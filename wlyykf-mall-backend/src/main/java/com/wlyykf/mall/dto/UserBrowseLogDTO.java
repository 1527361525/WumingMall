package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户浏览日志DTO
 */
@Data
public class UserBrowseLogDTO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 停留时长（秒）
     */
    private Integer stayDuration;
}
