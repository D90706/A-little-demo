package example.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.util.StrUtil;
import example.mapper.AdminMapper;
import example.entity.Admin;
import example.entity.Account;
import example.exception.CustomException;

import java.util.List;

//Service层负责接收来自Controller层的请求，并调用Mapper层的接口方法，将数据返回给Controller层。

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    // 新增管理员
    public void add(Admin admin) {
        String username = admin.getUsername();  // 账号
        Admin dbAdmin = adminMapper.selectByUsername(username);
        if (dbAdmin != null) {  // 注册的账号已存在 无法注册
            throw new CustomException("500", "账号已存在，请更换别的账号");
        }
        if (StrUtil.isBlank(admin.getPassword())) {  // 密码没填
            admin.setPassword("admin");  // 默认密码 123
        }
        if (StrUtil.isBlank(admin.getName())) {  // 名字没填
            admin.setName(admin.getUsername());  // 默认名称
        }
        // 一定要设置角色
        admin.setRole("ADMIN");  // 管理员的角色
        adminMapper.insert(admin);
    }

    // 查询所有管理员
    public List<Admin> selectAll() {
        List<Admin> list = adminMapper.selectAll(null);
        return list;
    }

    // 根据id查询管理员
    public Admin selectById(Integer id) {
        Admin admin = adminMapper.selectById(id);
        return admin;
    }

    // 分页查询
    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize, Admin admin) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    // 修改管理员信息
    public void update(Admin admin) {
        adminMapper.updateById(admin);
    }

    // 删除管理员
    public void delete(Integer id) {
        adminMapper.deleteById(id);
    }

    // 批量删除
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.delete(id);
        }
    }

    // 登录验证
    public Admin login(Account account) {
        String username = account.getUsername();
        String password = account.getPassword();
        Admin dbAdmin = adminMapper.selectByUsername(username);
        if (dbAdmin == null) {
            throw new CustomException("500", "该账号不存在");
        }
        if (!dbAdmin.getPassword().equals(password)) {
            throw new CustomException("500", "密码错误");
        }
        return dbAdmin;
    }

    public void updatePassword(Account account) {
        Integer id = account.getId();
        Admin admin = adminMapper.selectById(id);
        if(!admin.getPassword().equals(account.getPassword())){
            throw new CustomException("500", "原密码错误");
        }
        admin.setPassword(account.getNewPassword());
        adminMapper.updateById(admin);
    }
}
