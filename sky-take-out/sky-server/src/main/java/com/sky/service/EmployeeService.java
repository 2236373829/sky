package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     *  保存员工信息
     * @param employeeDTO
     * @return
     */
    boolean save(EmployeeDTO employeeDTO);

    /**
     * 根据条件分页查询员工信息
     * @param queryDTO
     * @return
     */
    PageResult page(EmployeePageQueryDTO queryDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    boolean switchStatus(Integer status, Long id);
}
