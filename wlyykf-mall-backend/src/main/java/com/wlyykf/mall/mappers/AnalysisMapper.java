package com.wlyykf.mall.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 数据分析Mapper接口
 */
public interface AnalysisMapper {

    /**
     * 获取所有用户的最后登录IP
     * @return 用户ID和IP地址列表
     */
    List<Map<String, Object>> getUserLastLoginIps();

    /**
     * 获取用户购买力分层统计
     * 按消费金额分档：低消费(<100)、中消费(100-500)、高消费(>500)
     * @return 各层级用户数量
     */
    List<Map<String, Object>> getUserPurchasingPowerDistribution();

    /**
     * 获取用户商品分类偏好统计
     * @param userId 用户ID
     * @return 各分类购买数量
     */
    List<Map<String, Object>> getUserCategoryPreference(@Param("userId") Long userId);
}
