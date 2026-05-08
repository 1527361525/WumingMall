package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_operation_log")
public class OperationLog extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long userId;

    private Integer userType;

    private String operationType;

    private String operationContent;

    private LocalDateTime operationTime;

    private String ipAddress;
}
