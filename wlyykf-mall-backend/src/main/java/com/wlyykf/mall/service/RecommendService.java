package com.wlyykf.mall.service;

import com.wlyykf.mall.vo.RecommendProductVO;
import com.wlyykf.mall.vo.ResponseVO;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendService {

    /**
     * 简单推荐：基于商品关联的推荐
     * "购买过此商品的人也买了..."
     *
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    ResponseVO<List<RecommendProductVO>> getSimpleRecommend(Long productId);

    /**
     * 协同过滤推荐：基于用户行为相似度的推荐
     * 使用Jaccard相似度计算用户间相似性
     * 新用户采用热门商品推荐作为冷启动方案
     *
     * @param userId 用户ID
     * @return 推荐商品列表（固定返回5个）
     */
    ResponseVO<List<RecommendProductVO>> getCollaborativeRecommend(Long userId);
}
