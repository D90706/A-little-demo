package example.mapper;
import java.util.List;

import example.entity.Admin;
import org.apache.ibatis.annotations.*;



public interface AdminMapper {

    // 插入管理员
    void insert(Admin admin);

    // 更新管理员信息
    void updateById(Admin admin);

    // 删除管理员
    void deleteById(Integer id);

    // 批量删除管理员
    void deleteBatch(@Param("ids") List<Integer> ids);

    // 查询所有管理员
    List<Admin> selectAll(Admin admin);

    // 根据账号查询管理员
    Admin selectByUsername(String username);

    // 根据id查询管理员
    Admin selectById(Integer id);
}
