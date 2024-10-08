package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/13 14:39
 */
@Slf4j
@Api(tags = "用户端购物车相关接口")
@RequestMapping("/user/shoppingCart")
@RestController
public class ShoppingCartController {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation(value = "添加购物车")
    @PostMapping("/add")
    public Result addCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，商品信息：{}", shoppingCartDTO);
        service.addCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查询购物车菜品信息
     *
     * @return
     */
    @ApiOperation(value = "查询购物车菜品信息")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> shoppingCartList = service.showShoppingCart();
        return Result.success(shoppingCartList);
    }

    /**
     * 购物车菜品-1
     *
     * @return
     */
    @ApiOperation(value = "购物车菜品-1")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        service.sub(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @return
     */@ApiOperation(value = "清空购物车")
    @DeleteMapping("/clean")
    public Result clean() {
        service.cleanShoppingCart();
        return Result.success();
    }

}
