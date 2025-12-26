package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.wlyykf.mall.entity.OrderItem;
import com.wlyykf.mall.vo.OrderItemVO;
import com.wlyykf.mall.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItem> {
    List<OrderItemVO> getOrderItemByOrderId(@Param("orderId") Long orderId);

    List<ProductVO> getProductTopN(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("topN") Integer n);
}
