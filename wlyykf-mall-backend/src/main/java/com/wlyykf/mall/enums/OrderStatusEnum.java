package com.wlyykf.mall.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    UNPAID(0, "待支付"),
    PAID(1, "已支付"),
    DELIVERED(2, "已发货"),
    FINISHED(3, "已完成");

    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(Integer code) {
        for (OrderStatusEnum value : OrderStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getMsg();
            }
        }
        return null;
    }
}
