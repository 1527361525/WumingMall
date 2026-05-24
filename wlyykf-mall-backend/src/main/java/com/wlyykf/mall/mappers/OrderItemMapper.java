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
}
