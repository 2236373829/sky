package com.sky.controller.user;

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
@RequestMapping("/user/shop")
@RestController
@Slf4j
public class UserShopController {

    public static final String KEY = "shopStatus";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "获取店铺状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }

}
