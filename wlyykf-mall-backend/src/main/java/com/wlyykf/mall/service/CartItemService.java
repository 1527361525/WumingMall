package com.wlyykf.mall.service;

import com.wlyykf.mall.dto.CartItemDTO;
import com.wlyykf.mall.vo.CartItemVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CartItemService {
    /**
     * 购物车商品新增
     * @param cartItemDTO 购物车商品信息
     */
    ResponseVO<Void> addCartItem(CartItemDTO cartItemDTO, Long userId);

    /**
     * 购物车商品删除
     * @param cartItemId 购物车商品ID
     */
    ResponseVO<Void> deleteCartItem(Long cartItemId);

    /**
     * 购物车商品修改
     * @param cartItemDTO 购物车商品信息
     */
    ResponseVO<Void> updateCartItem(CartItemDTO cartItemDTO);

    /**
     * 计算选中物品总价
     */
    ResponseVO<Map<String, Object>> getTotalPrice(Long userId);

    /**
     * 清空购物车
     */
    ResponseVO<Void> clearCart(Long userId);

    /**
     * 分页查询购物车所有商品
     * @param pageNum 页码
     * @param pageSize 页大小
     */
    PageResultVO<CartItemVO> getAll(Integer pageNum, Integer pageSize, Long userId);


}
