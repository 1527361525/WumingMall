package com.wlyykf.mall.controller;

import com.wlyykf.mall.service.AnalysisService;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器
 * 提供用户画像、销售趋势、异常监控等数据分析接口
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/analysis")
public class AnalysisController {

    @Resource
    private AnalysisService analysisService;

    /**
     * 用户地域分布统计
     * 基于用户最后登录IP解析省份进行统计
     * @return 各省份用户数量分布
     */
    @GetMapping("/user/regionDistribution")
    public ResponseVO<List<Map<String, Object>>> getUserRegionDistribution() {
        return analysisService.getUserRegionDistribution();
    }

    /**
     * 用户购买力分层统计
     * 按用户历史订单总金额分档：低消费(<100)、中消费(100-500)、高消费(>500)
     * @return 各消费层级用户数量
     */
    @GetMapping("/user/purchasingPower")
    public ResponseVO<List<Map<String, Object>>> getUserPurchasingPower() {
        return analysisService.getUserPurchasingPower();
    }

    /**
     * 用户偏好分类统计
     * 统计指定用户购买商品的分类偏好
     * @param userId 用户ID
     * @return 各分类购买数量统计
     */
    @GetMapping("/user/preference/{userId}")
    public ResponseVO<List<Map<String, Object>>> getUserPreference(
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        return analysisService.getUserPreference(userId);
    }

    /**
     * 销售趋势统计
     * 支持日/周/月三种时间粒度：
     * - day: 最近7天每日销售数据
     * - week: 最近5周每周销售数据
     * - month: 最近12个月每月销售数据
     * @param type 时间类型：day/week/month
     * @return 销售趋势数据列表
     */
    @GetMapping("/sales/trend")
    public ResponseVO<List<Map<String, Object>>> getSalesTrend(
            @RequestParam @NotBlank(message = "时间类型不能为空") String type) {
        return analysisService.getSalesTrend(type);
    }

    /**
     * 商品销售趋势统计
     * 获取指定商品的销售趋势数据
     * @param productId 商品ID
     * @param type 时间类型：day(最近7天)/week(最近5周)/month(最近12个月)
     * @return 商品销售趋势数据
     */
    @GetMapping("/product/trend/{productId}")
    public ResponseVO<List<Map<String, Object>>> getProductTrend(
            @PathVariable @NotNull(message = "商品ID不能为空") Long productId,
            @RequestParam @NotBlank(message = "时间类型不能为空") String type) {
        return analysisService.getProductTrend(productId, type);
    }

    /**
     * 类别销售统计
     * 统计各商品类别的销售数据（包括销售额、销售量、订单数）
     * @return 各类别销售统计数据
     */
    @GetMapping("/category/sales")
    public ResponseVO<List<Map<String, Object>>> getCategorySales() {
        return analysisService.getCategorySales();
    }

    /**
     * 销售异常检测
     * 检测今日销售额和订单量相比昨日同期及上周同日的波动情况
     * 波动超过阈值（默认30%）时标记为异常
     * @return 异常检测结果，包含今日/昨日/上周数据及波动率
     */
    @GetMapping("/abnormal/sales")
    public ResponseVO<Map<String, Object>> getSalesAbnormalDetection() {
        return analysisService.getSalesAbnormalDetection();
    }

    /**
     * 今日实时销售数据
     * 获取今日累计的订单数和销售额（从0点到当前时间）
     * @return 今日实时累计数据
     */
    @GetMapping("/abnormal/realtime")
    public ResponseVO<Map<String, Object>> getTodayRealtimeData() {
        return analysisService.getTodayRealtimeData();
    }

    /**
     * 用户画像概览统计
     * 获取总用户数、有购买记录用户数、人均消费金额
     * @return 用户画像概览统计数据
     */
    @GetMapping("/user/overview")
    public ResponseVO<Map<String, Object>> getUserPortraitOverview() {
        return analysisService.getUserPortraitOverview();
    }
}
