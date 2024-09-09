package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/7 11:02
 */
public interface DishService {

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     * @return
     */
    boolean save(DishDTO dishDTO);

    /**
     *根据条件分页查询菜品信息
     *
     * @param queryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO queryDTO);

    /**
     * 根据id删除菜品信息
     *
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     * @return
     */
    boolean update(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
