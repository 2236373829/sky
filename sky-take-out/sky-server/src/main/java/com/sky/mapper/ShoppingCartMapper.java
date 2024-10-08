package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/13 14:51
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询购物车
     *
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 购物车已经存在的商品+1
     *
     * @param shoppingCart
     */
    void updateById(ShoppingCart shoppingCart);

    /**
     * 添加购物车数据
     *
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            " values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据id删除购物车数据
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 清空购物车
     *
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void cleanShoppingCart(Long userId);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void batchInsert(List<ShoppingCart> shoppingCartList);
}
