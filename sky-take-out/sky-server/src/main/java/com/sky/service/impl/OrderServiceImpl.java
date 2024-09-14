package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:45
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private OrderMapper orderMapper;
    private OrderDetailMapper orderDetailMapper;
    private AddressBookMapper addressBookMapper;
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper,
                            AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    /**
     * 用户下单
     *
     * @param submitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO submitDTO) {
        // 0.处理各种业务异常（地址簿、购物车为空）
        AddressBook addressBook = addressBookMapper.getById(submitDTO.getAddressBookId());
        if (addressBook == null) { // 地址簿为空
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 查询当前用户的购物车信息
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(cart);
        if (shoppingCartList.isEmpty()) { // 购物车为空
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 1.向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(submitDTO, orders);
        orders.setOrderTime(LocalDateTime.now()); // 设置下单时间
        orders.setPayStatus(Orders.UN_PAID); // 设置订单支付状态 未支付
        orders.setStatus(Orders.PENDING_PAYMENT); // 设置订单状态 待付款
        orders.setNumber(String.valueOf(System.currentTimeMillis())); // 设置订单号
        orders.setPhone(addressBook.getPhone()); // 设置订单手机号
        orders.setConsignee(addressBook.getConsignee()); // 设置收货人
        orders.setUserId(userId); // 设置用户id
        orderMapper.insert(orders); // 插入订单数据

        // 2.向订单明细表插入n条数据
        BigDecimal orderAmount = new BigDecimal(0); // 订单总金额
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        shoppingCartList.forEach(shoppingCart -> {


            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId()); // 设置当前订单详情关联的订单id
            orderDetails.add(orderDetail);
        });
        orderDetailMapper.batchInsert(orderDetails);


        // 3.清空当前用户的购物车
        shoppingCartMapper.cleanShoppingCart(userId);

        // 4.封装vo返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }
}
