package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.enums.OrderStatusEnum;
import com.wlyykf.mall.mappers.OrderItemMapper;
import com.wlyykf.mall.mappers.OrderMapper;
import com.wlyykf.mall.service.StatisticService;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public ResponseVO<Map<String, Object>> getTotalAmount(Integer type) {
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = getStartDate(type);
        BigDecimal totalAmount = orderMapper.getTotalAmount(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        return ResponseVO.success("查询成功", result);
    }

    @Override
    public ResponseVO<List<ProductVO>> getProductTopN(Integer type, Integer n) {
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = getStartDate(type);
        List<ProductVO> productVOList = orderItemMapper.getProductTopN(startDate, endDate, n);
        return ResponseVO.success("查询成功", productVOList);
    }

    @Override
    public ResponseVO<List<Map<String, Object>>> getAllTypeOrderCount(Integer type) {
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = getStartDate(type);
        List<Map<String, Object>> orderCountList = orderMapper.getAllTypeOrderCount(startDate, endDate);

        for (Map<String, Object> orderCount : orderCountList) {
            Long code = (Long)orderCount.get("orderStatus");
            String orderStatusName = OrderStatusEnum.getMsgByCode(code.intValue());
            orderCount.put("orderStatusName", orderStatusName);
        }
        return ResponseVO.success("查询成功", orderCountList);
    }

    /**
     * 根据查询时间类型返回对应起始日期
     * @param type 查询时间类型 1-日 2-周 3-月 4-年
     */
    private LocalDate getStartDate(Integer type) {
        LocalDate now = LocalDate.now();
        if (type == 1) {
            // 返回当天日期
            return now;
        } else if (type == 2) {
            // 返回本周第一天
            return now.with(DayOfWeek.MONDAY);
        } else if (type == 3) {
            // 返回本月第一天
            return now.with(TemporalAdjusters.firstDayOfMonth());
        } else if (type == 4) {
            // 返回本年第一天
            return now.with(TemporalAdjusters.firstDayOfYear());
        }
        return null;
    }
}
