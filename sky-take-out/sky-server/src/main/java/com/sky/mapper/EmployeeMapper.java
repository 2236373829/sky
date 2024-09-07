package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFIll;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     * @return
     */
    @AutoFIll(value = OperationType.INSERT)
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_user, update_user)" +
            "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createUser}, #{updateUser})")
    int insert(Employee employee);

    /**
     * 根据条件分页查询员工信息
     * @param queryDTO
     * @return
     */
    Page<Employee> page(EmployeePageQueryDTO queryDTO);

    /**
     * 根据id动态修改属性
     * @param employee
     * @return
     */
    @AutoFIll(value = OperationType.UPDATE)
    int update(Employee employee);

    @Select("select id, name, username, phone, sex, id_number, status, create_time, update_time, create_user, update_user" +
            " from employee where id = #{id}")
    Employee getById(Long id);
}
