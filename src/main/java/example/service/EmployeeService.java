package example.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import example.mapper.EmployeeMapper;
import example.entity.Employee;
import java.util.List;

//Service层负责接收来自Controller层的请求，并调用Mapper层的接口方法，将数据返回给Controller层。

@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    // 新增员工
    public void add(Employee employee) {
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
}
