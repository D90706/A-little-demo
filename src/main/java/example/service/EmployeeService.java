package example.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.util.StrUtil;
import example.mapper.EmployeeMapper;
import example.entity.Employee;
import example.entity.Account;
import example.exception.CustomException;

import java.util.List;

//Service层负责接收来自Controller层的请求，并调用Mapper层的接口方法，将数据返回给Controller层。

@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    // 新增员工
   public void add(Employee employee) {
    String username = employee.getUsername();  // 账号
    Employee dbEmployee = employeeMapper.selectByUsername(username);
    if (dbEmployee != null) {  // 注册的账号已存在 无法注册
        throw new CustomException("500", "账号已存在，请更换别的账号");
    }
    if (StrUtil.isBlank(employee.getPassword())) {  // 密码没填
        employee.setPassword("123");  // 默认密码 123
    }
    if (StrUtil.isBlank(employee.getName())) {  // 名字没填
        employee.setName(employee.getUsername());  // 默认名称
    }
    // 一定要设置角色
    employee.setRole("EMP");  // 员工的角色
    employeeMapper.insert(employee);
}

    //查询所有员工
    public List<Employee> selectAll() {
        List<Employee> list = employeeMapper.selectAll(null);
        return list;
    }

    //根据id查询员工
    public Employee selectById(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        return employee;
    }

    //分页查询
    public PageInfo<Employee> selectPage(Integer pageNum, Integer pageSize,Employee employee) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return PageInfo.of(list);

    }

    //修改员工信息
	public void update(Employee employee) {
        employeeMapper.updateById(employee);
	}

    //删除员工
    public void delete(Integer id) {
        employeeMapper.deleteById(id);
    }

    //批量删除
    public void deleteBatch(List<Integer> ids) {
        for(Integer id : ids){
            this.delete(id);
        }
    }

    //登录验证
    public Employee login(Account account){
        String username = account.getUsername();
        String password = account.getPassword();
        Employee dbEmployee =  employeeMapper.selectByUsername(username);
        if(dbEmployee == null){
            throw new CustomException("500", "该账号不存在");
        }
        if(!dbEmployee.getPassword().equals(password)){
            throw new CustomException("500", "密码错误");
        }
        return dbEmployee;
    }

    public void register(Employee employee) {
        this.add(employee);
    }

    public void updatePassword(Account account) {
        Integer id = account.getId();
        Employee employee = employeeMapper.selectById(id);
        if(!employee.getPassword().equals(account.getPassword())){
            throw new CustomException("500", "原密码错误");
        }
        employee.setPassword(account.getNewPassword());
        employeeMapper.updateById(employee);
    }
}
