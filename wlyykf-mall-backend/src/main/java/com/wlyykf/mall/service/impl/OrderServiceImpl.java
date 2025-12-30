package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlyykf.mall.dto.OrderAddDTO;
import com.wlyykf.mall.dto.OrderBuyDTO;
import com.wlyykf.mall.dto.OrderListDTO;
import com.wlyykf.mall.dto.OrderUpdateDTO;
import com.wlyykf.mall.entity.Order;
import com.wlyykf.mall.entity.OrderItem;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.entity.User;
import com.wlyykf.mall.enums.OrderStatusEnum;
import com.wlyykf.mall.exception.BusinessException;
import com.wlyykf.mall.mappers.*;
import com.wlyykf.mall.service.*;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private CartItemMapper cartItemMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private CartItemService cartItemService;

    @Resource
    private EmailService emailService;

    @Resource
    private UserService userService;

    @Resource
    private IdentifierGenerator identifierGenerator;

    @Resource
    private CurrentUserUtil currentUserUtil;

    @Override
    public ResponseVO<Void> updateOrder(OrderUpdateDTO orderUpdateDTO) {
        Order order = new Order();
        order.setOrderId(orderUpdateDTO.getOrderId());
        order.setOrderStatus(orderUpdateDTO.getOrderStatus());
        order.setAddress(orderUpdateDTO.getAddress());
        if (orderMapper.updateById(order) > 0) {
            return ResponseVO.success("修改成功", null);
        }
        return ResponseVO.fail("修改失败", null);
    }

    @Override
    public ResponseVO<Void> deleteOrder(Long orderId) {
        if (orderMapper.deleteById(orderId) > 0) {
            return ResponseVO.success("删除成功", null);
        }
        return ResponseVO.fail("删除失败", null);
    }

    @Override
    public ResponseVO<Void> addOrder(OrderAddDTO orderAddDTO) {
        String orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderId(orderAddDTO.getOrderId());
        order.setOrderNo(orderNo);
        order.setUserId(orderAddDTO.getUserId());
        order.setTotalAmount(orderAddDTO.getTotalAmount());
        order.setOrderStatus(orderAddDTO.getOrderStatus());
        order.setAddress(orderAddDTO.getAddress());
        if (orderMapper.insert(order) > 0) {
            return ResponseVO.success("添加成功", null);
        }
        return ResponseVO.fail("添加失败", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> pay(Long userId, Long orderId) {
        User user = userMapper.selectById(userId);
        Order order = orderMapper.selectById(orderId);
        if (user == null || order == null) {
            throw new BusinessException(500, "参数错误");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(500, "不能支付他人的订单");
        }

        // 判断用户余额是否足够支付该订单
        BigDecimal userMoney = user.getMoney();
        if (userMoney.compareTo(order.getTotalAmount()) >= 0) {
            // 扣除用户余额
            user.setMoney(userMoney.subtract(order.getTotalAmount()));
            userMapper.updateById(user);

            // 增加销量，扣减库存
            List<OrderItem> cartItemList = orderItemService.getCartItemByOrderId(orderId);
            for (OrderItem cartItem : cartItemList) {
                Product product = productMapper.selectById(cartItem.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    product.setTotalSales(product.getTotalSales() + cartItem.getQuantity());
                    productMapper.updateById(product);
                }
            }

            // 修改订单状态为已支付
            order.setOrderStatus(OrderStatusEnum.PAID.getCode());
            order.setPaymentTime(LocalDateTime.now());
            orderMapper.updateById(order);

            return ResponseVO.success("支付成功", null);
        }
        return ResponseVO.fail("支付失败", null);
    }

    @Override
    public ResponseVO<OrderDetailVO> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            orderDetailVO.setOrderNo(order.getOrderNo());
            orderDetailVO.setTotalAmount(order.getTotalAmount());
            orderDetailVO.setOrderStatus(OrderStatusEnum.getMsgByCode(order.getOrderStatus()));
            orderDetailVO.setAddress(order.getAddress());
            orderDetailVO.setPaymentTime(order.getPaymentTime());
            orderDetailVO.setCreateTime(order.getCreateTime());

            // 获取订单下所有商品
            orderDetailVO.setProducts(orderItemMapper.getOrderItemByOrderId(orderId));

            return ResponseVO.success("获取成功", orderDetailVO);
        }
        return ResponseVO.fail("订单不存在", null);
    }

    @Override
    public PageResultVO<OrderVO> getOrderList(OrderListDTO orderListDTO) {
        Page<OrderVO> page = new Page<>(orderListDTO.getPageNum(), orderListDTO.getPageSize());

        LocalDate endDate = orderListDTO.getEndDate();
        if (endDate != null) {
            // 结束时间加1天，便于查询
            endDate = endDate.plusDays(1);
            orderListDTO.setEndDate(endDate);
        }

        Page<OrderVO> orderList = orderMapper.getOrderList(page, orderListDTO);

        for (OrderVO orderVO : orderList.getRecords()) {
            orderVO.setOrderStatusMsg(OrderStatusEnum.getMsgByCode(orderVO.getOrderStatus()));
        }

        return PageResultVO.build(orderList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> submitOrder(Long userId, String address) {
        // 得到购物车所有物品
        List<CartItemVO> allCartItem = cartItemMapper.getAllList(userId);

        // 预判断库存是否充足
        for (CartItemVO cartItem : allCartItem) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product.getStock() < cartItem.getQuantity()) {
                String msg = "商品 [" + product.getName() + "] 库存不足";
                throw new BusinessException(500, msg);
            }
        }

        // 清空购物车
        cartItemService.clearCart(userId);

        // 创建订单
        Long orderId = (Long) identifierGenerator.nextId(null);
        OrderAddDTO orderAddDTO = new OrderAddDTO();
        orderAddDTO.setUserId(userId);
        orderAddDTO.setTotalAmount(allCartItem.stream()
                .map(CartItemVO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderAddDTO.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
        orderAddDTO.setAddress(address);
        orderAddDTO.setOrderId(orderId);
        addOrder(orderAddDTO);

        // 加入订单物品表
        List<OrderItem> orderItems = allCartItem.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setProductId(Long.valueOf(cartItem.getProductId()));
                    orderItem.setProductName(cartItem.getProductName());
                    orderItem.setProductImage(cartItem.getProductImage());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setTotalPrice(cartItem.getTotalPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());
        orderItemService.saveBatch(orderItems);

        return ResponseVO.success("提交成功", null);
    }

    @Override
    public ResponseVO<Void> deliver(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            // 检查订单状态是否为已付款
            if (!order.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                throw new BusinessException(500, "订单状态错误");
            }

            order.setOrderStatus(OrderStatusEnum.DELIVERED.getCode());
            orderMapper.updateById(order);

            // 发送电子邮件确认发货
            Long orderOwnerId = order.getUserId();
            User orderOwner = userMapper.selectById(orderOwnerId);
            if (orderOwner == null) {
                throw new BusinessException(500, "用户不存在");
            }

            log.info("发送发货邮件给用户：{}", orderOwner.getEmail());
            emailService.sendDeliveryEmail(orderOwner.getEmail(), order.getOrderNo());

            return ResponseVO.success("发货成功", null);
        }
        return ResponseVO.fail("订单不存在", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> buy(OrderBuyDTO orderBuyDTO) {

        Long userId = orderBuyDTO.getUserId();
        // 预判断库存是否充足
        Product product = productMapper.selectById(orderBuyDTO.getProductId());
        if (product.getStock() < orderBuyDTO.getQuantity()) {
            String msg = "商品 [" + product.getName() + "] 库存不足";
            throw new BusinessException(500, msg);
        }

        // 计算总金额
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(orderBuyDTO.getQuantity()));

        // 创建订单
        Long orderId = (Long) identifierGenerator.nextId(null);
        OrderAddDTO orderAddDTO = new OrderAddDTO();
        orderAddDTO.setUserId(userId);
        orderAddDTO.setTotalAmount(totalAmount);
        orderAddDTO.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
        orderAddDTO.setAddress(orderBuyDTO.getAddress());
        orderAddDTO.setOrderId(orderId);
        addOrder(orderAddDTO);

        // 加入订单物品表
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(product.getProductId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getProductImage());
        orderItem.setPrice(product.getPrice());
        orderItem.setTotalPrice(totalAmount);
        orderItem.setQuantity(orderBuyDTO.getQuantity());
        orderItemMapper.insert(orderItem);

        return ResponseVO.success("购买成功", null);
    }

    @Override
    public PageResultVO<OrderVO> getOrderListForAdmin(OrderListDTO orderListDTO) {
        // 校验当前用户是否是管理员
        boolean isAdmin = (boolean) userService.isAdmin().get("isAdmin");
        if (!isAdmin) {
            throw new BusinessException(500, "当前用户无权限访问");
        }

        Page<OrderVO> page = new Page<>(orderListDTO.getPageNum(), orderListDTO.getPageSize());

        LocalDate endDate = orderListDTO.getEndDate();
        if (endDate != null) {
            // 结束时间加1天，便于查询
            endDate = endDate.plusDays(1);
            orderListDTO.setEndDate(endDate);
        }

        Page<OrderVO> orderList = orderMapper.getOrderList(page, orderListDTO);

        // 提取用户id列表
        List<Long> userIdList = orderList.getRecords().stream()
                .map(OrderVO::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息
        List<UserVO> userList = userService.getUsersByIds(userIdList);

        Map<Long, UserVO> userMap = userList.stream()
                .collect(Collectors.toMap(
                        userVO -> Long.valueOf(userVO.getUserId()),
                        userVO -> userVO
                ));

        for (OrderVO orderVO : orderList.getRecords()) {
            orderVO.setOrderStatusMsg(OrderStatusEnum.getMsgByCode(orderVO.getOrderStatus()));
            orderVO.setNickName(userMap.get(orderVO.getUserId()).getNickName());
        }

        return PageResultVO.build(orderList);
    }


    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");
        String timeStr = LocalDateTime.now().format(formatter);

        String randomStr = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));

        return timeStr + randomStr;
    }
}
