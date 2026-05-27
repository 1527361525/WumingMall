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

    /**
     * 获取销售趋势统计
     * @param type 时间类型：day-日趋势(最近7天), week-周趋势(最近5周), month-月趋势(最近12个月)
     * @return 销售趋势数据列表
     */
    ResponseVO<List<Map<String, Object>>> getSalesTrend(String type);

    /**
     * 获取商品销售趋势
     * @param productId 商品ID
     * @param type 时间类型：day-日趋势(最近7天), week-周趋势(最近5周), month-月趋势(最近12个月)
     * @return 商品销售趋势数据
     */
    ResponseVO<List<Map<String, Object>>> getProductTrend(Long productId, String type);

    /**
     * 获取类别销售统计
     * @return 各类别销售数据
     */
    ResponseVO<List<Map<String, Object>>> getCategorySales();

    /**
     * 获取销售异常检测结果
     * 检测今日销售额和订单量相比昨日同期的波动情况
     * @return 异常检测结果，包含今日数据、昨日数据、波动率及是否异常标记
     */
    ResponseVO<Map<String, Object>> getSalesAbnormalDetection();

    /**
     * 获取今日实时销售数据
     * @return 今日实时累计订单数和销售额
     */
    ResponseVO<Map<String, Object>> getTodayRealtimeData();

    /**
     * 获取用户画像概览统计数据
     * @return 包含总用户数、有购买记录用户数、人均消费金额的数据
     */
    ResponseVO<Map<String, Object>> getUserPortraitOverview();
}
