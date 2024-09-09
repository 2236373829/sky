package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author xyzZero3
 * @date 2024/9/8 15:43
 */
@Api(tags = "店铺相关接口")
@RequestMapping("/admin/shop")
@RestController
@Slf4j
public class AdminShopController {

    public static final String KEY = "shopStatus";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "设置店铺状态")
    @PutMapping("/{status}")
    public Result shopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态：{}", status == 1 ? "营业中" : "已打烊");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @ApiOperation(value = "获取店铺状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }

}
