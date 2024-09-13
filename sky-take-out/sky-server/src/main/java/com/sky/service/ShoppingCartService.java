package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

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

    /**
     * 查询购物车菜品信息
     *
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 购物车菜品-1
     *
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     *
     */
    void cleanShoppingCart();
}
