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
}
