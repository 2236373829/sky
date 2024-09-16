package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
