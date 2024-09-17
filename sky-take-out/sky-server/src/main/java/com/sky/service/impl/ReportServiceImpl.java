package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author xyzZero3
 * @date 2024/9/16 20:50
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>(); // 当前集合用于存放从begin到end范围内的日期
        dateList.add(begin);

        // 计算begin到end的日期
        while (!begin.isAfter(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateListString = StringUtils.join(dateList, ","); // 拼接日期

        ArrayList<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> { // 查询对应日期的营业额（状态为已完成的订单金额）
            Double turnover = orderMapper.sumByMap(date, Orders.COMPLETED);
            turnover = turnover == null ? 0.00 : turnover;
            turnoverList.add(turnover);
        });
        String turnoverString = StringUtils.join(turnoverList, ","); // 拼接对应日期的营业额

        return TurnoverReportVO.builder()
                .dateList(dateListString) // 设置统计的日期范围
                .turnoverList(turnoverString) // 设置对应日期的营业额
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>(); // 存放从begin到end之间的每天对应的日期
        dateList.add(begin);
        while (!begin.isAfter(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateListString = StringUtils.join(dateList, ","); // 日期字符串

        List<Integer> newUserList = new ArrayList<>(); // 每天新用户数量
        List<Integer> totalUserList = new ArrayList<>(); // 总用户数量

        dateList.forEach(date -> {
            LocalDateTime beforeToday = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("today", date);
            Integer newUser = userMapper.countByMap(map); // 当日新增用户数量
            newUserList.add(newUser);

            map.clear();
            map.put("beforeToday", beforeToday);
            Integer totalUser = userMapper.countByMap(map); // 总用户数量
            totalUserList.add(totalUser);
        });

        String newUserListString = StringUtils.join(newUserList, ",");
        String totalUserListString = StringUtils.join(totalUserList, ",");

        return UserReportVO.builder()
                .dateList(dateListString) // 设置日期字符串
                .newUserList(newUserListString) // 设置每日新用户数量
                .totalUserList(totalUserListString) // 设置用户总量
                .build();
    }
}
