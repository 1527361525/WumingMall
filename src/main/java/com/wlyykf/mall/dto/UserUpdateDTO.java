package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserUpdateDTO {
    @NotNull
    private Long userId;

    @NotBlank
    private String nickName;

    private String phone;

    private String avatar;
}
