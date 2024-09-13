package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

/**
 * @author xyzZero3
 * @date 2024/9/13 14:42
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void addCart(ShoppingCartDTO shoppingCartDTO);

}
