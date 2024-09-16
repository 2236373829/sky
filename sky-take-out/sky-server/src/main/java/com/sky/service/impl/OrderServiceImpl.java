package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:45
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserMapper userMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final WebSocketServer webSocketServer;

    private Orders orders;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper,
                            AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper,
                            UserMapper userMapper, WeChatPayUtil weChatPayUtil, WebSocketServer webSocketServer) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.userMapper = userMapper;
        this.weChatPayUtil = weChatPayUtil;
        this.webSocketServer = webSocketServer;
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
        this.orders = orders;
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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer orderPaidStatus = Orders.PAID; // 支付状态：已支付
        Integer orderStatus = Orders.TO_BE_CONFIRMED; // 订单状态：待接单

        // 发现没有将支付时间check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        // 获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(orderStatus, orderPaidStatus, check_out_time, orderNumber);

        // 通过websocket向客户端浏览器推送消息
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 1); // 1表示来单提醒 2表示客户催单
        map.put("orderId", this.orders.getId());
        map.put("content", "订单号：" + this.orders.getNumber());

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 客户催单
     *
     * @param id
     */
    @Override
    public void reminder(Long id) {
        // 根据id查询订单数据
        Orders orders = orderMapper.getById(id);

        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 2); // 1表示来但提醒 2表示客户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        String jsonString = JSON.toJSONString(map);

        // 通过websocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(jsonString);
    }
}
