package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:44
 */
public interface OrderService {


    /**
     * 用户下单
     *
     * @param submitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO submitDTO);
}
