package com.wlyykf.mall.service;

import com.wlyykf.mall.entity.CartItem;
import com.wlyykf.mall.entity.OrderItem;
import com.wlyykf.mall.vo.CartItemVO;

import java.util.List;

public interface OrderItemService {

    /**
     * 批量保存
     */
    boolean saveBatch(List<OrderItem> orderItems);

    /**
     * 根据订单ID查询订单下所有商品
     * @param orderId 订单ID
     */
    List<OrderItem> getCartItemByOrderId(Long orderId);
}
