package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.mappers.OrderItemMapper;
import com.wlyykf.mall.service.RecommendService;
import com.wlyykf.mall.vo.RecommendProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 推荐服务实现类
 */
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 简单推荐：基于商品关联的推荐
     * "购买过此商品的人也买了..."
     *
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    @Override
    public ResponseVO<List<RecommendProductVO>> getSimpleRecommend(Long productId) {
        List<RecommendProductVO> recommendList = orderItemMapper.getSimpleRecommend(productId);
        return ResponseVO.success(recommendList);
    }
}
