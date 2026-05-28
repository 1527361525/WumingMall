package com.wlyykf.mall.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 操作日志查询DTO
 */
@Data
public class OperationLogQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 10;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
}
