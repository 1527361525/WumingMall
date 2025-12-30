package com.wlyykf.mall.controller;

import com.wlyykf.mall.dto.CartItemDTO;
import com.wlyykf.mall.service.CartItemService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.vo.CartItemVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/cartItem")
public class CartItemController {

    @Resource
    private CartItemService cartItemService;

    @Resource
    private CurrentUserUtil currentUserUtil;

    /**
     * 购物车商品新增
     * @param cartItemDTO 购物车商品信息
     */
    @PostMapping
    public ResponseVO<Void> addCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return cartItemService.addCartItem(cartItemDTO, userId);
    }

    /**
     * 购物车商品删除
     * @param cartItemId 购物车商品ID
     */
    @PutMapping("/{cartItemId}")
    public ResponseVO<Void> deleteCartItem(@PathVariable @NotNull Long cartItemId) {
        return cartItemService.deleteCartItem(cartItemId);
    }

    /**
     * 购物车商品修改
     * @param cartItemDTO 购物车商品信息
     */
    @PutMapping
    public ResponseVO<Void> updateCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) {
        return cartItemService.updateCartItem(cartItemDTO);
    }

    /**
     * 计算选中物品总价
     */
    @GetMapping("/totalPrice")
    public ResponseVO<Map<String, Object>> getTotalPrice() {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return cartItemService.getTotalPrice(userId);
    }

    /**
     * 清空购物车
     */
    @PutMapping("/clear")
    public ResponseVO<Void> clearCart() {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return cartItemService.clearCart(userId);
    }

    /**
     * 分页查询购物车所有商品
     * @param pageNum 页码
     * @param pageSize 页大小
     */
    @GetMapping("getAll")
    public PageResultVO<CartItemVO> getAll(@RequestParam Integer pageNum,
                                           @RequestParam Integer pageSize) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return cartItemService.getAll(pageNum, pageSize, userId);
    }
}
