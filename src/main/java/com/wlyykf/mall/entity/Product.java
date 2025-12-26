package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_product")
public class Product extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long productId;

    private String name;

    private Long categoryId;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private String productImage;

    private Integer totalSales;
}
