package com.wlyykf.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wlyykf.mall.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private String orderNo;

    private BigDecimal totalAmount;

    private String orderStatus;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private List<OrderItemVO> products;

}
