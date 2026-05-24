package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.wlyykf.mall.entity.OrderItem;
import com.wlyykf.mall.vo.OrderItemVO;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.RecommendProductVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItem> {
    List<OrderItemVO> getOrderItemByOrderId(@Param("orderId") Long orderId);

    List<ProductVO> getProductTopN(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("topN") Integer n);

    /**
     * 获取指定类别下销量前N的商品
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param topN 前N个
     * @param categoryId 类别ID
     * @return 商品列表
     */
    List<ProductVO> getProductTopNByCategory(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("topN") Integer n,
                                             @Param("categoryId") Long categoryId);

    /**
     * 简单推荐：购买过此商品的用户还购买的其他商品
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    List<RecommendProductVO> getSimpleRecommend(@Param("productId") Long productId);

    /**
     * 获取与用户购买行为相似的用户列表（基于共同购买的商品数）
     * @param userId 目标用户ID
     * @param limit 限制返回数量
     * @return 相似用户ID列表及共同购买商品数
     */
    List<RecommendProductVO.SimilarUser> findSimilarUsers(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取用户已购买的商品ID列表
     * @param userId 用户ID
     * @return 商品ID列表
     */
    List<Long> getUserPurchasedProductIds(@Param("userId") Long userId);

    /**
     * 获取相似用户购买过但目标用户未购买的商品
     * @param userId 目标用户ID
     * @param similarUserIds 相似用户ID列表
     * @param limit 限制返回数量
     * @return 推荐商品列表
     */
    List<RecommendProductVO> getCollaborativeRecommend(@Param("userId") Long userId,
                                                       @Param("similarUserIds") List<Long> similarUserIds,
                                                       @Param("limit") Integer limit);

    /**
     * 获取热门商品推荐（用于新用户冷启动）
     * @param limit 限制返回数量
     * @return 热门商品列表
     */
    List<RecommendProductVO> getHotProducts(@Param("limit") Integer limit);
}
