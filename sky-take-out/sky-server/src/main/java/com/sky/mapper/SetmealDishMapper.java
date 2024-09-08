package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/7 16:09
 */
@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关系
     *
     * @param setmealDishList
     */
    void batchInsert(List<SetmealDish> setmealDishList);

    /**
     * 批量删除套餐菜品关系
     *
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据套餐id删除信息
     *
     * @param id
     */
    void deleteBySetmealId(Long id);
}
