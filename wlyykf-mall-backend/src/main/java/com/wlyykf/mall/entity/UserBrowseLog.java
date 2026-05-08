package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user_browse_log")
public class UserBrowseLog extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long userId;

    private Long productId;

    private Long categoryId;

    private LocalDateTime browseTime;

    private Integer stayDuration;

    private String ipAddress;
}
