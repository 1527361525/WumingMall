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

    /**
     * 获取销售趋势统计（日趋势-最近7天）
     * @return 每日销售数据
     */
    List<Map<String, Object>> getSalesTrendDaily();

    /**
     * 获取销售趋势统计（周趋势-最近5周）
     * @return 每周销售数据
     */
    List<Map<String, Object>> getSalesTrendWeekly();

    /**
     * 获取销售趋势统计（月趋势-最近12个月）
     * @return 每月销售数据
     */
    List<Map<String, Object>> getSalesTrendMonthly();

    /**
     * 获取商品销售趋势（日趋势-最近7天）
     * @param productId 商品ID
     * @return 商品每日销售数据
     */
    List<Map<String, Object>> getProductTrendDaily(@Param("productId") Long productId);

    /**
     * 获取商品销售趋势（周趋势-最近5周）
     * @param productId 商品ID
     * @return 商品每周销售数据
     */
    List<Map<String, Object>> getProductTrendWeekly(@Param("productId") Long productId);

    /**
     * 获取商品销售趋势（月趋势-最近12个月）
     * @param productId 商品ID
     * @return 商品每月销售数据
     */
    List<Map<String, Object>> getProductTrendMonthly(@Param("productId") Long productId);

    /**
     * 获取类别销售统计
     * @return 各类别销售数据
     */
    List<Map<String, Object>> getCategorySalesStats();

    /**
     * 获取今日实时销售数据（从当日0点到当前时间）
     * @return 今日订单数和销售额
     */
    Map<String, Object> getTodayRealtimeSales();

    /**
     * 获取昨日同期销售数据（昨日0点到当前时间的对应时刻）
     * @return 昨日同期订单数和销售额
     */
    Map<String, Object> getYesterdaySamePeriodSales();

    /**
     * 获取上周同日销售数据（上周同日0点到24点）
     * @return 上周同日订单数和销售额
     */
    Map<String, Object> getLastWeekSameDaySales();

    /**
     * 获取总用户数
     * @return 总用户数量
     */
    Long getTotalUserCount();

    /**
     * 获取有购买记录的用户数
     * @return 有购买记录的用户数量
     */
    Long getPurchaseUserCount();

    /**
     * 获取人均消费金额
     * @return 人均消费金额
     */
    java.math.BigDecimal getAverageConsumption();
}
