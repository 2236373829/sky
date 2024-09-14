package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:55
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情数据
     *
     * @param orderDetails
     */
    void batchInsert(ArrayList<OrderDetail> orderDetails);
}
