package com.wlyykf.mall.dto;

import lombok.Data;

@Data
public class TokenUserInfoDTO {
    private Long userId;
    private String nickName;
    private Long expireAt;
    private String token;
}
