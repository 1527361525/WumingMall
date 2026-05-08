package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user_login_log")
public class UserLoginLog extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long userId;

    private Integer userType;

    private LocalDateTime loginTime;

    private String ipAddress;
}
