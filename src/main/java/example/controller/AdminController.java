package example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import example.common.Result;
import example.entity.Admin;
import example.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//controller层，处理来自前端的请求，返回json数据

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 添加管理员信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success("数据添加成功");
    }

    /**
     * 通过id删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        adminService.delete(id);
        return Result.success("数据删除成功");
    }

    /**
     * 批量删除
     */
    @DeleteMapping("deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        adminService.deleteBatch(ids);
        return Result.success("数据批量删除成功");
    }

    /**
     * 查询所有管理员信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        // 调用service层方法查询所有管理员信息
        List<Admin> list = adminService.selectAll();
        // 返回封装完成后的数据
        return Result.success(list);
    }

    // 通过路径传递参数
    // @PathVariable对应路径:端口号/接口号/参数/参数
    // @RequestParam对应路径:端口号/接口号?参数名=参数值&参数名=参数值
    @GetMapping("selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        // 调用service层方法查询所有管理员信息
        Admin admin = adminService.selectById(id);
        // 返回封装完成后的数据
        return Result.success(admin);
    }

    @GetMapping("selectByOne")
    public Result selectByOne(@RequestParam Integer id) {
        // 调用service层方法查询所有管理员信息
        Admin admin = adminService.selectById(id);
        // 返回封装完成后的数据
        return Result.success(admin);
    }

    @GetMapping("selectPage")
    public Result selectPage(
            Admin admin,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Admin> pageInfo = adminService.selectPage(pageNum, pageSize, admin);
        return Result.success(pageInfo);
    }

    /**
     * 修改管理员信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) {
        adminService.update(admin);
        return Result.success("数据修改成功");
    }
}
