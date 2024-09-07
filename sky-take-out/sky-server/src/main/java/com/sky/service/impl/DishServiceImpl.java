package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/7 11:02
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper flavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    @Override
    public boolean save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表插入数据
        int dishInsert = dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        // 向口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            int flavorBatchInsert = flavorMapper.batchInsert(flavors);
        }

        return false;
    }

    /**
     * 根据条件分页查询菜品信息
     *
     * @param queryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<DishVO> dishVOPage = dishMapper.page(queryDTO);
        return new PageResult(dishVOPage.getTotal(), dishVOPage.getResult());
    }

    /**
     * 根据id删除菜品信息
     *
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public int deleteByIds(List<Long> ids) {
        // 菜品起售中不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 菜品被套餐关联不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        /*// 删除菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            // 删除菜品口味数据
            flavorMapper.deleteByDishId(id);
        }*/

        // 根据菜品id集合批量删除菜品信息
        dishMapper.deleteByIds(ids);

        // 根据菜品id集合批量删除口味信息
        flavorMapper.deleteByDishIds(ids);

        return 0;
    }

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        return dishMapper.selectById(id);
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     * @return
     */
    @Override
    public boolean update(DishDTO dishDTO) {
        // 修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        int dishUpdate = dishMapper.update(dish);

        if (dishUpdate > 0) {
            // 删除所有口味信息
            flavorMapper.deleteByDishId(dishDTO.getId());

            // 添加新的口味信息
            List<DishFlavor> flavors = dishDTO.getFlavors();
            if (flavors != null && flavors.size() > 0) {
                flavors.forEach(flavor -> {
                    flavor.setDishId(dishDTO.getId());
                });
            }
            int flavorInsert = flavorMapper.batchInsert(flavors);
            return true;
        }

        return false;
    }
}
