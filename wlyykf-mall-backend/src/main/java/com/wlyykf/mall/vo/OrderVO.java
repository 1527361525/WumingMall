package com.wlyykf.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private String orderId;

    private String orderNo;

    private BigDecimal totalAmount;

    private Integer orderStatus;

    private String orderStatusMsg;

    private Long userId;

    private String nickName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
