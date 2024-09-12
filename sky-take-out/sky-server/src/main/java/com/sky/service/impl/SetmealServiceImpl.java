package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/8 15:51
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.id")
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 向套餐表中插入数据
        setmealMapper.insert(setmeal);

        // 获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        // 向套餐菜品关系表中插入数据
        setmealDishMapper.batchInsert(setmealDishList);
    }

    /**
     * 根据条件分页查询
     *
     * @param setmealDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealDTO) {
        // 开始分页
        PageHelper.startPage(setmealDTO.getPage(), setmealDTO.getPageSize());

        Page<SetmealVO> setmealVOPage = setmealMapper.page(setmealDTO);
        return new PageResult(setmealVOPage.getTotal(), setmealVOPage.getResult());
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public void batchDelete(List<Long> ids) {
        ids.forEach(id -> {
            SetmealVO setmealVO = setmealMapper.getById(id);
            // 起售中的套餐不能删除
            if (StatusConstant.ENABLE.equals(setmealVO.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        // 删除套餐和套餐关联的食品数据
        setmealMapper.batchDelete(ids); // 删除套餐信息

        setmealDishMapper.batchDelete(ids); // 删除套餐相关菜品信息
    }

    /**
     * 根据id获取套餐信息
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        return setmealMapper.getById(id);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改套餐信息
        setmealMapper.update(setmeal);

        // 删除套餐菜品关联表数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        // 添加套餐菜品关联数据
        setmealDTO.getSetmealDishes().forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
        });
        setmealDishMapper.batchInsert(setmealDTO.getSetmealDishes());

    }

    /**
     * 套餐起售停售
     *
     * @param status
     * @param id
     */
    @Override
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public void updateStatus(Integer status, Long id) {
        // 起售套餐时，判断套餐内是否有停售菜品，有停售菜品时不允许起售
        if (status.equals(StatusConstant.ENABLE)) {
            // 获取套菜内的菜品信息
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            dishList.forEach(dish -> {
                if (dish.getStatus().equals(StatusConstant.DISABLE)) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }

        // 修改套餐状态
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId
     * @return
     */
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId") // key: setmealCache::100
    @Override
    public List<Setmeal> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        return setmealMapper.list(setmeal);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemById(id);
    }
}
