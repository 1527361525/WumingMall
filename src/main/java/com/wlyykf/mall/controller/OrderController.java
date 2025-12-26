package com.wlyykf.mall.controller;

import com.wlyykf.mall.dto.OrderAddDTO;
import com.wlyykf.mall.dto.OrderBuyDTO;
import com.wlyykf.mall.dto.OrderListDTO;
import com.wlyykf.mall.dto.OrderUpdateDTO;
import com.wlyykf.mall.service.OrderService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.vo.OrderDetailVO;
import com.wlyykf.mall.vo.OrderVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private CurrentUserUtil currentUserUtil;

    /**
     * 修改订单
     * @param orderUpdateDTO 修改信息
     */
    @PutMapping
    public ResponseVO<Void> updateOrder(@Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {
        return orderService.updateOrder(orderUpdateDTO);
    }

    /**
     * 删除订单
     * @param orderId 订单ID
     */
    @PutMapping("/{orderId}")
    public ResponseVO<Void> deleteOrder(@PathVariable @NotNull Long orderId) {
        return orderService.deleteOrder(orderId);
    }

    /**
     * 新增订单
     * @param orderAddDTO 新增信息
     */
    @PostMapping
    public ResponseVO<Void> addOrder(@Valid @RequestBody OrderAddDTO orderAddDTO) {
        return orderService.addOrder(orderAddDTO);
    }

    /**
     * 付款
     * @param orderId 订单ID
     */
    @PostMapping("/pay")
    public ResponseVO<Void> pay(@RequestParam @NotNull Long orderId) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return orderService.pay(userId, orderId);
    }

    /**
     * 获取订单详情
     * @param orderId 订单ID
     */
    @GetMapping("/{orderId}")
    public ResponseVO<OrderDetailVO> getOrderDetail(@PathVariable @NotNull Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 分页查询订单
     * @param orderListDTO 查询条件
     */
    @PostMapping("/getOrderList")
    public PageResultVO<OrderVO> getOrderList(@Valid @RequestBody OrderListDTO orderListDTO) {
        if (orderListDTO.getUserId() == null) {
            Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
            orderListDTO.setUserId(userId);
        }
        return orderService.getOrderList(orderListDTO);
    }

    /**
     * 分页查询订单（管理）
     * @param orderListDTO 查询条件
     */
    @PostMapping("/admin/getOrderList")
    public PageResultVO<OrderVO> getOrderListForAdmin(@Valid @RequestBody OrderListDTO orderListDTO) {
        return orderService.getOrderListForAdmin(orderListDTO);
    }

    /**
     * 下单
     * @param address 收货地址
     */
    @PostMapping("/submitOrder")
    public ResponseVO<Void> addOrder(@RequestParam @NotBlank String address) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        return orderService.submitOrder(userId, address);
    }

    /**
     * 订单发货
     * @param orderId 订单ID
     */
    @PostMapping("/deliver")
    public ResponseVO<Void> deliver(@RequestParam @NotNull Long orderId) {
        return orderService.deliver(orderId);
    }

    /**
     * 商品立即购买
     * @param orderBuyDTO 购买信息
     */
    @PostMapping("/buy")
    public ResponseVO<Void> buy(@Valid @RequestBody OrderBuyDTO orderBuyDTO) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        orderBuyDTO.setUserId(userId);
        return orderService.buy(orderBuyDTO);
    }

}
