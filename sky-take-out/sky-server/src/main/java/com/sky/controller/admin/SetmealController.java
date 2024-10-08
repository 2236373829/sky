package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/8 15:43
 */
@Slf4j
@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService service;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增套餐接口")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        service.save(setmealDTO);
        return Result.success();
    }

    /**
     * 根据条件分页查询
     *
     * @param setmealDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "根据条件分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealDTO) {
        PageResult pageResult = service.page(setmealDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        service.batchDelete(ids);
        return Result.success();
    }

    /**
     * 根据id获取套餐信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取套餐信息")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = service.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        service.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        service.updateStatus(status, id);
        return Result.success();
    }

}






