package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.dto.OrderListDTO;
import com.wlyykf.mall.entity.Order;
import com.wlyykf.mall.vo.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<Order> {
    Page<OrderVO> getOrderList(Page<OrderVO> page, @Param("query") OrderListDTO orderListDTO);

    BigDecimal getTotalAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> getAllTypeOrderCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
