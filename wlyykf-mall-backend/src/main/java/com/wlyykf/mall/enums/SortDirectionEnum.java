package com.wlyykf.mall.enums;

import lombok.Getter;

@Getter
public enum SortDirectionEnum {
    ASC(0, "ASC", "升序"),
    DESC(1, "DESC", "降序");

    private Integer code;
    private String name;
    private String desc;

    SortDirectionEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static SortDirectionEnum getByCode(Integer code) {
        for (SortDirectionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
