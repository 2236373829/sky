package com.sky.service;

import com.sky.dto.DishDTO;

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

}
