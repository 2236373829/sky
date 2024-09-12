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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @Transactional
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表插入数据
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        // 向口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            flavorMapper.batchInsert(flavors);
        }

        // 清除缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        this.cleanCache(key);
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

        // 将所有菜品缓存数据清除，所有以"dish_"开头的key
        this.cleanCache("dish_*");

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
                flavorMapper.batchInsert(flavors);
            }

            // 将所有菜品缓存数据清除，所有以"dish_"开头的key
            this.cleanCache("dish_*");

            return true;
        }

        return false;
    }

    /**
     * 起售或停售菜品
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        dishMapper.startOrStop(status, id);
        // 将所有菜品缓存数据清除，所有以"dish_"开头的key
        this.cleanCache("dish_*");
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.listByCategoryId(categoryId);
    }

    /**
     * 条件查询菜品和口味
     *
     * @param categoryId
     * @return
     */
    public List<DishVO> listWithFlavor(Long categoryId) {
        // 构造redis的菜品key
        String key = "dish_" + categoryId;

        // 查询redis中是否存在菜品数据
        List<DishVO> dishCache = (ArrayList<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishCache != null && dishCache.size() != 0) {
            // 如果存在直接返回，无需查询数据库
            return dishCache;
        }

        // 如果不存在，查询数据库并放入redis中
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = flavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        // 查询到的数据放入数据库中
        redisTemplate.opsForValue().set(key, dishVOList);

        return dishVOList;
    }

    /**
     * 清理缓存数据
     *
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
