package com.wlyykf.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserVO {
    private String userId;

    private String nickName;

    private String email;

    private String phone;

    private String avatar;

    private BigDecimal money;
}
