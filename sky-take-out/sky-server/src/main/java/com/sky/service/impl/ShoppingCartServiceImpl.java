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

            cart.setNumber(cart.getNumber() + 1); // 数量+1
            // 修改菜品价格
            if (cart.getDishId() != null) {
                BigDecimal dishPrice = dishMapper.getById(cart.getDishId()).getPrice();
                cart.setAmount(dishPrice.multiply(new BigDecimal(cart.getNumber()))); // 设置新的菜品金额
            } else {
                // 修改套餐价格
                BigDecimal setmealPrice = setmealMapper.getById(cart.getSetmealId()).getPrice();
                cart.setAmount(setmealPrice.multiply(new BigDecimal(cart.getNumber()))); // 设置新的套餐金额
            }
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

    /**
     * 查询购物车菜品信息
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return mapper.list(shoppingCart);
    }

    /**
     * 购物车菜品-1
     */
    @Override
    public void sub(ShoppingCartDTO cartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(cartDTO, shoppingCart);

        ShoppingCart cart = mapper.list(shoppingCart).get(0); // 获取当前菜品或套餐信息

        if (cart.getNumber() == 1) { // 如果菜品数量为1，就删除当前数据
            mapper.deleteById(cart.getId());

        } else { // 如果不为1，则进行-1操作
            int dishNumber = cart.getNumber() - 1; // 数量-1
            cart.setNumber(dishNumber); // 设置购物车菜品数量

            if (cart.getDishId() != null) { // 修改菜品价格
                BigDecimal dishPrice = dishMapper.getById(cart.getDishId()).getPrice();
                BigDecimal dishAmount = dishPrice.multiply(new BigDecimal(dishNumber)); // 数量-1后的总价
                cart.setAmount(dishAmount);

            } else { // 修改套餐价格
                BigDecimal setmealPrice = setmealMapper.getById(cart.getSetmealId()).getPrice();
                BigDecimal setmealAmount = setmealPrice.multiply(new BigDecimal(dishNumber)); // 数量-1后的总价
                cart.setAmount(setmealAmount);
            }
            mapper.updateById(cart);
        }
    }
}
