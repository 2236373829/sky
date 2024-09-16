package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/16 20:50
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;

    public ReportServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
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
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();

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
}
