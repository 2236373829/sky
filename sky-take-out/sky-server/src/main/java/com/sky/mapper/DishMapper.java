package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFIll;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {


    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 插入菜品数据
     *
     * @param dish
     * @return
     */
    @AutoFIll(value = OperationType.INSERT)
    void insert(Dish dish);


    /**
     *  根据条件分页查询菜品信息
     *
     * @param queryDTO
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO queryDTO);

    /**
     * 根据id获取菜品信息
     *
     * @param id
     * @return
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user," +
            " update_user from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品信息
     *
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据菜品id集合批量删除菜品
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    DishVO selectById(Long id);

    /**
     * 修改菜品信息
     *
     * @param dish
     * @return
     */
    @AutoFIll(OperationType.UPDATE)
    int update(Dish dish);

    /**
     * 起售或停售商品
     *
     * @param status
     * @param id
     */
    @Update("update dish set status = #{status} where id = #{id}")
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     *
     * List<Dish>
     * @param categoryId
     * @return
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user " +
            "from dish where category_id = #{categoryId} and status = 1")
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 根据条件查询菜品信息
     *
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id获取菜品列表
     *
     * @param id
     * @return
     */
    @Select("select * from dish left join setmeal_dish on dish.id = setmeal_dish.dish_id where setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);

}
