package com.wlyykf.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志VO
 */
@Data
public class OperationLogVO {

    /**
     * 日志ID
     */
    private String logId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户类型 0-用户 1-销售人员 2-管理者
     */
    private Integer userType;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
