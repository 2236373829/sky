package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/14 21:46
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     *
     * @param orderStatus
     * @param orderPaidStatus
     * @param checkOutTime
     * @param orderNumber
     */
    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{checkOutTime}" +
            " where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, String orderNumber);

    /**
     * 根据状态和下单时间查询订单信息
     *
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status," +
            " amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time," +
            " estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status" +
            " from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status," +
            " amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status from orders where id = #{id}")
    Orders getById(Long id);
}
