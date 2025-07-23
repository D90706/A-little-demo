package example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import example.common.Result;
import example.entity.Account;
import example.entity.Employee;
import example.exception.CustomException;
import jakarta.annotation.Resource;
import example.service.EmployeeService;
import example.service.AdminService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class WebController {

    @GetMapping("/hello")
    public Result hello() {
        // throw new CustomException("400","错误请求，禁止访问");
        return Result.success("Hello World!");
    }

    @Resource
    private EmployeeService employeeService;

    @Resource
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account result = null;
        if ("Admin".equals(account.getRole())) { // 管理员登录
            result = adminService.login(account);
        } else if ("Employee".equals(account.getRole())) {
            result = employeeService.login(account);
        }
        System.out.println("登录成功：" + result);
        return Result.success(result);
    }

    // 注册接口
    @PostMapping("/register")
    public Result register(@RequestBody Employee employee) {
        try {
            employeeService.register(employee);
            return Result.success("注册成功");
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        }
    }

    // 修改密码接口
    @PutMapping("/updatePassword/{id}")
    public Result updatePassword(@RequestBody Account account) {
        try {
           
            if ("ADMIN".equals(account.getRole())) { // 管理员登录
                adminService.updatePassword(account); 
            } else if ("EMP".equals(account.getRole())) {
                employeeService.updatePassword(account);
            }
            return Result.success("修改密码成功");

        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        }
    }

}
