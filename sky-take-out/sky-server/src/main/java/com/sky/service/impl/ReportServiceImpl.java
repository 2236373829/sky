package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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

        String newUserString = StringUtils.join(newUserList, ",");
        String totalUserString = StringUtils.join(totalUserList, ",");

        return UserReportVO.builder()
                .dateList(dateListString) // 设置日期字符串
                .newUserList(newUserString) // 设置每日新用户数量
                .totalUserList(totalUserString) // 设置用户总量
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>(); // 存放从begin到end之间的每天对应的日期
        dateList.add(begin);
        while (!begin.isAfter(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateListString = StringUtils.join(dateList, ","); // 日期字符串

        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        ArrayList<Integer> totalOrderCountList = new ArrayList<>();

        // 遍历dateList集合，查询每天的有效订单和订单总数
        dateList.forEach(date -> {
            HashMap<String, Object> map = new HashMap<>();
            // 查询每天订单总数
            map.put("today", date);
            Integer validOrderCount = orderMapper.orderCount(map);
            totalOrderCountList.add(validOrderCount);

            // 查询每天有效订单
            map.put("status", Orders.COMPLETED);
            Integer totalOrderCount = orderMapper.orderCount(map);
            validOrderCountList.add(totalOrderCount);
        });

        Integer validOrderCount = validOrderCountList.stream().mapToInt(Integer::intValue).sum(); // 有效订单数
        String validOrderCountString = StringUtils.join(validOrderCountList, ","); // 有效订单数据

        Integer totalOrderCount = totalOrderCountList.stream().mapToInt(Integer::intValue).sum(); // 总订单数
        String totalOrderCountListString = StringUtils.join(totalOrderCountList, ","); // 总订单数据

        Double orderCompletionRate = totalOrderCount == 0 ? validOrderCount.doubleValue() / totalOrderCount.doubleValue() : 0.0; // 订单完成率

        return OrderReportVO.builder()
                .dateList(dateListString)
                .validOrderCount(validOrderCount)
                .validOrderCountList(validOrderCountString)
                .totalOrderCount(totalOrderCount)
                .orderCountList(totalOrderCountListString)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10Statistics(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX));

        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameListString = StringUtils.join(nameList, ","); // 拼接name字符串

        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberListString = StringUtils.join(numberList, ","); // 拼接number字符串

        return SalesTop10ReportVO.builder()
                .nameList(nameListString)
                .numberList(numberListString)
                .build();
    }
}
