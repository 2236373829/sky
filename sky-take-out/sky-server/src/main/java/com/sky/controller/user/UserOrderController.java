package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:41
 */
@Api(tags = "用户订单管理")
@RequestMapping("/user/order")
@RestController
public class UserOrderController {

    private final OrderService orderService;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户下单
     *
     * @param submitDTO
     * @return
     */
    @ApiOperation(value = "用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO submitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(submitDTO);
        return Result.success(orderSubmitVO);
    }

}
