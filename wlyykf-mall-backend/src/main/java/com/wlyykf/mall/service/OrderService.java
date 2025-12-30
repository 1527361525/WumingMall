package com.wlyykf.mall.service;

import com.wlyykf.mall.dto.OrderAddDTO;
import com.wlyykf.mall.dto.OrderBuyDTO;
import com.wlyykf.mall.dto.OrderListDTO;
import com.wlyykf.mall.dto.OrderUpdateDTO;
import com.wlyykf.mall.vo.OrderDetailVO;
import com.wlyykf.mall.vo.OrderVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;

import java.util.List;

public interface OrderService {
    /**
     * 修改订单
     * @param orderUpdateDTO 修改信息
     */
    ResponseVO<Void> updateOrder(OrderUpdateDTO orderUpdateDTO);

    /**
     * 删除订单
     * @param orderId 订单ID
     */
    ResponseVO<Void> deleteOrder(Long orderId);

    /**
     * 新增订单
     * @param orderAddDTO 新增信息
     */
    ResponseVO<Void> addOrder(OrderAddDTO orderAddDTO);

    /**
     * 付款
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    ResponseVO<Void> pay(Long userId, Long orderId);

    /**
     * 获取订单详情
     * @param orderId 订单ID
     */
    ResponseVO<OrderDetailVO> getOrderDetail(Long orderId);

    /**
     * 分页查询订单
     * @param orderListDTO 查询条件
     */
    PageResultVO<OrderVO> getOrderList(OrderListDTO orderListDTO);

    /**
     * 下单
     * @param userId 用户ID
     * @param address 收货地址
     */
    ResponseVO<Void> submitOrder(Long userId, String address);

    /**
     * 订单发货
     * @param orderId 订单ID
     */
    ResponseVO<Void> deliver(Long orderId);

    /**
     * 商品立即购买
     * @param orderBuyDTO 购买信息
     */
    ResponseVO<Void> buy(OrderBuyDTO orderBuyDTO);

    /**
     * 分页查询订单（管理）
     * @param orderListDTO 查询条件
     */
    PageResultVO<OrderVO> getOrderListForAdmin(OrderListDTO orderListDTO);
}
