package com.wlyykf.mall.controller;

import com.wlyykf.mall.service.RecommendService;
import com.wlyykf.mall.vo.RecommendProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 推荐系统控制器
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    /**
     * 简单推荐：基于商品关联的推荐
     * "购买过此商品的人也买了..."
     *
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    @GetMapping("/simple/{productId}")
    public ResponseVO<List<RecommendProductVO>> getSimpleRecommend(
            @PathVariable @NotNull Long productId) {
        return recommendService.getSimpleRecommend(productId);
    }

    /**
     * 协同过滤推荐：基于用户行为相似度的推荐
     * 使用Jaccard相似度计算用户间相似性
     * 新用户采用热门商品推荐作为冷启动方案
     *
     * @param userId 用户ID
     * @return 推荐商品列表（固定返回5个）
     */
    @GetMapping("/collaborative/{userId}")
    public ResponseVO<List<RecommendProductVO>> getCollaborativeRecommend(
            @PathVariable @NotNull Long userId) {
        return recommendService.getCollaborativeRecommend(userId);
    }
}
