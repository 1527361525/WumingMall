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
}
