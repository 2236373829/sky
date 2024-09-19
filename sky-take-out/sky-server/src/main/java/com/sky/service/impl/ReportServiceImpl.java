package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xyzZero3
 * @date 2024/9/16 20:50
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    private final WorkspaceService workspaceService;

    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper, WorkspaceService workspaceService) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.workspaceService = workspaceService;
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
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
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
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map); // 总用户数量
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map); // 当日新增用户数量
            newUserList.add(newUser);

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
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询每天订单总数
            map.put("begin", beginTime);
            map.put("end", endTime);
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

        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount.doubleValue(); // 订单完成率

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

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {
        // 查询数据库，获取营业数据
        LocalDate dateBegin = LocalDate.now().minusMonths(1);
        LocalDate dateEnd = LocalDate.now();
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(dateBegin, dateEnd);

        // 通过POI将数据写入excel文件中
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/ExcelTemplate.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream); // 基于模版文件创建一个新的excel文件

            // 填充概览数据
            XSSFSheet sheet = excel.getSheetAt(0); // 获取excel的sheet页
            XSSFRow row2 = sheet.getRow(1);
            row2.getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd); // 填充时间段

            XSSFRow row4 = sheet.getRow(3);
            row4.getCell(2).setCellValue(businessDataVO.getTurnover()); // 填充营业额
            row4.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate()); // 订单完成率
            row4.getCell(6).setCellValue(businessDataVO.getNewUsers()); // 新增用户数

            XSSFRow row5 = sheet.getRow(4);
            row5.getCell(2).setCellValue(businessDataVO.getValidOrderCount()); // 有效订单数
            row5.getCell(4).setCellValue(businessDataVO.getUnitPrice()); // 平均客单价

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                // 查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(date, date);
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString()); // 日期
                row.getCell(2).setCellValue(businessData.getTurnover()); // 营业额
                row.getCell(3).setCellValue(businessData.getValidOrderCount()); // 有效订单数
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate()); // 订单完成率
                row.getCell(5).setCellValue(businessData.getUnitPrice()); // 平均客单价
                row.getCell(6).setCellValue(businessData.getNewUsers()); // 新增用户数\
            }

            // 通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            // 关闭资源
            outputStream.close();
            excel.close();
            inputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
