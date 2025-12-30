package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_cart_item")
public class CartItem extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private Integer quantity;

    private Integer selected;
}
