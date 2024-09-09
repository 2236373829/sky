package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFIll;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 插入套餐数据
     *
     * @param setmeal
     */
    @AutoFIll(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 根据条件分页查询
     *
     * @param setmealDTO
     * @return
     */
    Page<SetmealVO> page(SetmealPageQueryDTO setmealDTO);

    /**
     * 根据id获取套餐信息
     *
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 批量删除套餐数据
     *
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 修改套餐信息
     *
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * 根据分类id查询套餐
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
