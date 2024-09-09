package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/7 11:20
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     * @return
     */
    int batchInsert(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味信息
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);

    /**
     * 根据菜品id集合批量删除口味信息
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询口味
     *
     * @param id
     * @return
     */
    @Select("select id, dish_id, name, value from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
