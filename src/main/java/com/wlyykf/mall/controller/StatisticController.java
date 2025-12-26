package com.wlyykf.mall.controller;

import com.wlyykf.mall.service.StatisticService;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/statistic")
public class StatisticController {

    @Resource
    private StatisticService statisticService;

    /**
     * 获取总销售额
     * @param type 1-日 2-周 3-月 4-年
     * @return 总销售额
     */
    @GetMapping("/getTotalAmount")
    public ResponseVO<Map<String, Object>> getTotalAmount(@RequestParam @NotNull Integer type) {
        return statisticService.getTotalAmount(type);
    }

    /**
     * 获取销量前n的商品
     * @param type 1-日 2-周 3-月 4-年
     * @param n 前n
     * @return 销售量前N的商品列表
     */
    @GetMapping("/getProductTopN")
    public ResponseVO<List<ProductVO>> getProductTopN(@RequestParam @NotNull Integer type,
                                                      @RequestParam @NotNull Integer n) {
        return statisticService.getProductTopN(type, n);
    }

    /**
     * 获取所有类型的订单数量
     * @param type 1-日 2-周 3-月 4-年
     * @return 包含类型名和数量的订单列表
     */
    @GetMapping("/getAllTypeOrderCount")
    public ResponseVO<List<Map<String, Object>>> getAllTypeOrderCount(@RequestParam @NotNull Integer type) {
        return statisticService.getAllTypeOrderCount(type);
    }
}
