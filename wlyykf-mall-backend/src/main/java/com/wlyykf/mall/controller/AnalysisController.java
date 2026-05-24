package com.wlyykf.mall.controller;

import com.wlyykf.mall.service.AnalysisService;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
}
