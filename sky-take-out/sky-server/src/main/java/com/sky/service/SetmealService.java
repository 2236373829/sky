package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/8 15:50
 */
public interface SetmealService {
    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 根据条件分页查询
     *
     * @param setmealDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealDTO);

    /**
     * 批量删除
     *
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据id获取套餐信息
     *
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 套餐起售停售
     *
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);
}
