package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/13 14:42
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper mapper;

    private final DishMapper dishMapper;

    private final SetmealMapper setmealMapper;

    public ShoppingCartServiceImpl(ShoppingCartMapper mapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
        this.mapper = mapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入购物车的商品是否已经在购物车内
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> shoppingCartList = mapper.list(shoppingCart);

        if (!shoppingCartList.isEmpty()) { // 如果已经存在，只需要将数量+1
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);
            cart.setAmount(cart.getAmount().multiply(new BigDecimal(cart.getNumber()))); // 设置新的金额

            mapper.updateById(cart);

        } else { // 不存在则添加购物车数据

            // 判断本次添加的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                // 添加菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                // 添加套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            mapper.insert(shoppingCart);

        }
    }
}
