package com.wlyykf.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterDTO {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "昵称不能为空")
    private String nickName;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;

}
