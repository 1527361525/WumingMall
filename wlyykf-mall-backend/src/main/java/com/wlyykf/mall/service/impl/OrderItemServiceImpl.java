package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlyykf.mall.entity.CartItem;
import com.wlyykf.mall.entity.OrderItem;
import com.wlyykf.mall.mappers.OrderItemMapper;
import com.wlyykf.mall.service.OrderItemService;
import com.wlyykf.mall.vo.CartItemVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Override
    public boolean saveBatch(List<OrderItem> orderItems) {
        if (orderItems != null && orderItems.size() > 0) {
            return super.saveBatch(orderItems);
        }
        return false;
    }

    @Override
    public List<OrderItem> getCartItemByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = super.list(queryWrapper);
        return orderItems;
    }
}

