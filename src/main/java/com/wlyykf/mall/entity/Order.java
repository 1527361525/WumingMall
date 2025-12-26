package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_order")
public class Order extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderId;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private Integer orderStatus;

    private LocalDateTime paymentTime;

    private String address;
}
