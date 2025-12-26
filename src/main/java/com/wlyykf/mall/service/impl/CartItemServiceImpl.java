package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.dto.CartItemDTO;
import com.wlyykf.mall.entity.CartItem;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.exception.BusinessException;
import com.wlyykf.mall.mappers.CartItemMapper;
import com.wlyykf.mall.mappers.ProductMapper;
import com.wlyykf.mall.service.CartItemService;
import com.wlyykf.mall.vo.CartItemVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Resource
    private CartItemMapper cartItemMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public ResponseVO<Void> addCartItem(CartItemDTO cartItemDTO, Long userId) {
        Long productId = cartItemDTO.getProductId();

        // 判断数量是否合法
        if (cartItemDTO.getQuantity() <= 0) {
            throw new BusinessException(500, "参数错误");
        }

        // 判断购物车是否已有该商品
        CartItem dbCartItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));
        if (dbCartItem != null) {
            throw new BusinessException(500, "购物车已有该商品");
        }

        Product product = productMapper.selectById(productId);

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(cartItemDTO.getQuantity()));
        cartItem.setTotalPrice(totalPrice);
        cartItem.setProductId(cartItemDTO.getProductId());
        cartItem.setProductName(product.getName());
        cartItem.setProductImage(product.getProductImage());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        if (cartItemMapper.insert(cartItem) > 0) {
            return ResponseVO.success("添加成功",  null);
        }
        return ResponseVO.fail("添加失败", null);
    }

    @Override
    public ResponseVO<Void> deleteCartItem(Long cartItemId) {
        if (cartItemMapper.deleteById(cartItemId) > 0) {
            return ResponseVO.success("删除成功", null);
        }
        return ResponseVO.fail("删除失败", null);
    }

    @Override
    public ResponseVO<Void> updateCartItem(CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemMapper.selectById(cartItemDTO.getCartItemId());
        if (cartItem == null) {
            throw new BusinessException(500, "参数错误");
        }

        // 修改了数量，需要重新计算总价
        if (cartItemDTO.getQuantity() != null) {
            cartItem.setQuantity(cartItemDTO.getQuantity());
            // 如果数量为0，则删除该商品
            if (cartItemDTO.getQuantity() == 0) {
                deleteCartItem(cartItemDTO.getCartItemId());
                return ResponseVO.success("修改成功", null);
            }
            cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItemDTO.getQuantity())));
        }

        // 修改了选中状态
        if (cartItemDTO.getSelected() != null) {
            cartItem.setSelected(cartItemDTO.getSelected());
        }

        if (cartItemMapper.updateById(cartItem) > 0) {
            return ResponseVO.success("修改成功", null);
        }
        return ResponseVO.fail("修改失败", null);
    }

    @Override
    public ResponseVO<Map<String, Object>> getTotalPrice(Long userId) {
        BigDecimal totalPrice = cartItemMapper.getTotalPrice(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalPrice", totalPrice);
        return ResponseVO.success(result);
    }

    @Override
    public ResponseVO<Void> clearCart(Long userId) {
        LambdaUpdateWrapper<CartItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CartItem::getUserId, userId);
        updateWrapper.eq(CartItem::getDelFlag, 0);
        updateWrapper.set(CartItem::getDelFlag, 1);
        if (cartItemMapper.update(null, updateWrapper) > 0) {
            return ResponseVO.success("清空成功", null);
        }
        return ResponseVO.fail("清空失败", null);
    }

    @Override
    public PageResultVO<CartItemVO> getAll(Integer pageNum, Integer pageSize, Long userId) {
        Page<CartItemVO> page = Page.of(pageNum, pageSize);
        Page<CartItemVO> cartItemVOPage = cartItemMapper.getAll(page, userId);
        return PageResultVO.build(cartItemVOPage);
    }

}
