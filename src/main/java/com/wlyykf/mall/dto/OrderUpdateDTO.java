package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderUpdateDTO {
    private Long orderId;

    @NotNull(message = "订单状态不能为空")
    private Integer orderStatus;

    private String address;
}
