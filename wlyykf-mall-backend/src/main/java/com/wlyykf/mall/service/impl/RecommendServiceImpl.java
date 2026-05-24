package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.mappers.OrderItemMapper;
import com.wlyykf.mall.service.RecommendService;
import com.wlyykf.mall.vo.RecommendProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 推荐数量
     */
    private static final int RECOMMEND_LIMIT = 5;

    /**
     * 相似用户数量限制
     */
    private static final int SIMILAR_USER_LIMIT = 10;

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

    /**
     * 协同过滤推荐：基于用户行为相似度的推荐
     * 使用Jaccard相似度计算用户间相似性
     * 新用户采用热门商品推荐作为冷启动方案
     *
     * @param userId 用户ID
     * @return 推荐商品列表（固定返回5个）
     */
    @Override
    public ResponseVO<List<RecommendProductVO>> getCollaborativeRecommend(Long userId) {
        log.info("开始协同过滤推荐，用户ID: {}", userId);

        // 1. 检查用户是否有购买记录
        List<Long> userPurchasedProducts = orderItemMapper.getUserPurchasedProductIds(userId);
        log.info("用户 {} 已购买商品数量: {}", userId, userPurchasedProducts.size());

        // 2. 如果用户没有购买记录，使用热门商品作为冷启动推荐
        if (userPurchasedProducts.isEmpty()) {
            log.info("用户 {} 无购买记录，使用热门商品冷启动推荐", userId);
            List<RecommendProductVO> hotProducts = orderItemMapper.getHotProducts(RECOMMEND_LIMIT);
            return ResponseVO.success(hotProducts);
        }

        // 3. 查找相似用户（基于共同购买的商品数）
        List<RecommendProductVO.SimilarUser> similarUsers = orderItemMapper.findSimilarUsers(userId, SIMILAR_USER_LIMIT);
        log.info("找到 {} 个相似用户", similarUsers.size());

        // 4. 如果没有找到相似用户，也使用热门商品推荐
        if (similarUsers.isEmpty()) {
            log.info("未找到相似用户，使用热门商品推荐");
            List<RecommendProductVO> hotProducts = orderItemMapper.getHotProducts(RECOMMEND_LIMIT);
            return ResponseVO.success(hotProducts);
        }

        // 5. 提取相似用户ID列表
        List<Long> similarUserIds = similarUsers.stream()
                .map(RecommendProductVO.SimilarUser::getUserId)
                .collect(Collectors.toList());

        // 6. 获取协同过滤推荐商品（相似用户购买过但目标用户未购买的商品）
        List<RecommendProductVO> recommendations = orderItemMapper.getCollaborativeRecommend(
                userId, similarUserIds, RECOMMEND_LIMIT);

        log.info("协同过滤推荐商品数量: {}", recommendations.size());

        // 7. 如果推荐数量不足，补充热门商品
        if (recommendations.size() < RECOMMEND_LIMIT) {
            int needMore = RECOMMEND_LIMIT - recommendations.size();
            log.info("推荐数量不足，需要补充 {} 个热门商品", needMore);

            List<Long> existingProductIds = recommendations.stream()
                    .map(RecommendProductVO::getProductId)
                    .collect(Collectors.toList());

            List<RecommendProductVO> hotProducts = orderItemMapper.getHotProducts(RECOMMEND_LIMIT + needMore);

            for (RecommendProductVO hotProduct : hotProducts) {
                if (recommendations.size() >= RECOMMEND_LIMIT) {
                    break;
                }
                // 避免重复推荐
                if (!existingProductIds.contains(hotProduct.getProductId())
                        && !userPurchasedProducts.contains(hotProduct.getProductId())) {
                    recommendations.add(hotProduct);
                }
            }
        }

        return ResponseVO.success(recommendations);
    }
}
