package com.wlyykf.mall.service;

import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface StatisticService {
    ResponseVO<Map<String, Object>> getTotalAmount(Integer type);

    ResponseVO<List<ProductVO>> getProductTopN(Integer type, Integer n);

    ResponseVO<List<Map<String, Object>>> getAllTypeOrderCount(Integer type);

    /**
     * 获取指定类别下销量前N的商品
     * @param type 时间类型 1-日 2-周 3-月 4-年
     * @param n 前n
     * @param categoryId 类别ID
     * @return 商品列表
     */
    ResponseVO<List<ProductVO>> getProductTopNByCategory(Integer type, Integer n, Long categoryId);
}
