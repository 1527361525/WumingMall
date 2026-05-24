package com.wlyykf.mall.service;

import com.wlyykf.mall.vo.ResponseVO;

import java.util.List;
import java.util.Map;

/**
 * 数据分析服务接口
 */
public interface AnalysisService {

    /**
     * 获取用户地域分布统计
     * @return 各省份用户数量分布
     */
    ResponseVO<List<Map<String, Object>>> getUserRegionDistribution();

    /**
     * 获取用户购买力分层统计
     * @return 各消费层级用户数量
     */
    ResponseVO<List<Map<String, Object>>> getUserPurchasingPower();

    /**
     * 获取用户偏好分类统计
     * @param userId 用户ID
     * @return 各分类购买数量统计
     */
    ResponseVO<List<Map<String, Object>>> getUserPreference(Long userId);
}
