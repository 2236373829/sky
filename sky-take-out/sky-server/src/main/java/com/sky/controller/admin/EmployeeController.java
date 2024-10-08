package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 保存新的员工信息
     *
     * @param employeeDTO
     * @return
     */
    @ApiOperation(value = "保存新的员工信息")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        boolean save = employeeService.save(employeeDTO);
        return Result.success(save);
    }

    /**
     *  根据条件分页查询
     * @param queryDTO
     * @return
     */
    @ApiOperation(value = "员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO queryDTO) {
        PageResult page = employeeService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result switchStatus(@PathVariable Integer status, Long id) {
        boolean update = employeeService.switchStatus(status, id);
        return Result.success(update);
    }

    @ApiOperation(value = "根据id查询员工信息")
    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    @ApiOperation(value = "编辑员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        boolean update = employeeService.update(employeeDTO);
        return Result.success(update);
    }

}
