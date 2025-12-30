package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user")
public class User extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    private String nickName;

    private String email;

    private String phone;

    private String password;

    private String avatar;

    private BigDecimal money;
}
