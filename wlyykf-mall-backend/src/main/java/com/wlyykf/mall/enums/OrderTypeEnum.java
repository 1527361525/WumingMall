package com.wlyykf.mall.enums;

import lombok.Getter;

@Getter
public enum OrderTypeEnum {
    PRODUCT_TIME(0, "create_time", "上架时间"),
    PRODUCT_PRICE(1, "price", "商品价格"),
    PRODUCT_SALES(2, "total_sales", "商品销量");

    private Integer code;
    private String name;
    private String desc;

    OrderTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static OrderTypeEnum getByCode(Integer code) {
        for (OrderTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
