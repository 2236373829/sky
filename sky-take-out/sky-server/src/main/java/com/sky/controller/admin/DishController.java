package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理接口
 *
 * @author xyzZero3
 * @date 2024/9/7 10:59
 */
@Api(tags = "菜品管理接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService service;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        boolean save = service.save(dishDTO);
        return Result.success(save);
    }

    /**
     * 根据条件分页查询菜品信息
     *
     * @param queryDTO
     * @return
     */
    @ApiOperation(value = "根据条件分页查询菜品信息")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO queryDTO) {
        log.info("菜品分页查询：{}", queryDTO);
        PageResult pageResult = service.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除菜品信息
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "根据id删除菜品信息")
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        int delete = service.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询菜品信息")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品信息：{}", id);
        DishVO dishVO = service.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "修改菜品信息")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        boolean update = service.update(dishDTO);
        return Result.success(update);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = service.list(categoryId);
        return Result.success(list);
    }

}



