package certification.testtoken.service;

import certification.testtoken.entity.User;
import certification.testtoken.exception.CustomException;
import certification.testtoken.common.Result;
import jakarta.annotation.Resource;
import certification.testtoken.mapper.WebMapper;

import org.springframework.stereotype.Service;

@Service
public class WebService {

    @Resource
    private WebMapper webMapper;

    public Result login(User user) {
        User userResult = webMapper.login(user);
        if (userResult == null) {
            return Result.error("500", "用户或密码错误");
        } else {
            return Result.success("登录成功");
        }
    }

    public Result registration(User user) {
        String username = user.getUsername(); // 账号
        System.out.println("注册的账号" + username);
        User dbUser = webMapper.selectByUsername(username);
        if (dbUser != null) { // 注册的账号已存在 无法注册
            throw new CustomException("500", "账号已存在，请更换别的账号");
        }
        webMapper.insert(user);
        return Result.success("注册成功");
    }
}
