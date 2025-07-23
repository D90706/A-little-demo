package example.mapper;
import java.util.List;
import example.entity.Employee;

//Mapper负责数据库操作，定义对应的SQL语句，并将结果转换为Java对象

public interface EmployeeMapper {
    
    List<Employee> selectAll(Employee employee);

    Employee selectById(Integer id);

    void insert(Employee employee);

    void updateById(Employee employee);

    void deleteById(Integer id);

    Employee selectByUsername(String username);
}
