package com.wlyykf.mall.service;

/**
 * 日志服务接口
 */
public interface LogService {

    /**
     * 记录用户登录日志
     *
     * @param userId   用户ID
     * @param userType 用户类型 0-用户 1-销售人员 2-管理者
     * @param ip       IP地址
     */
    void recordLoginLog(Long userId, Integer userType, String ip);

    /**
     * 记录用户登出日志（使用操作日志表）
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @param ip       IP地址
     */
    void recordLogoutLog(Long userId, Integer userType, String ip);

    /**
     * 记录操作日志
     *
     * @param userId          用户ID
     * @param userType        用户类型
     * @param operationType   操作类型
     * @param operationContent 操作内容
     * @param ip              IP地址
     */
    void recordOperationLog(Long userId, Integer userType, String operationType, String operationContent, String ip);

    /**
     * 记录用户浏览日志
     *
     * @param userId       用户ID
     * @param productId    商品ID
     * @param categoryId   商品分类ID
     * @param stayDuration 停留时长（秒）
     * @param ip           IP地址
     */
    void recordBrowseLog(Long userId, Long productId, Long categoryId, Integer stayDuration, String ip);
}
