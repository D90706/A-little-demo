package certification.testtoken.controller;

import certification.testtoken.entity.User;
import certification.testtoken.exception.CustomException;
import certification.testtoken.common.Result;
import certification.testtoken.util.JedisUtil;
import certification.testtoken.util.JwtUtils;
import certification.testtoken.service.WebService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class WebController {

    @Resource
    private WebService webService;

    @Resource // 注入JwtUtils（替代手动new，符合Spring依赖注入规范）
    private JwtUtils jwtUtils;

    // 添加一个私有方法用于哈希加密密码
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法进行哈希加密", e);
        }
    }

    // 测试接口
    @GetMapping("/hello")
    public Result hello() {
        return Result.success("Hello World!");
    }

    // 登录接口
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        System.out.println("用户账号" + user.getUsername());
        System.out.println("用户密码" + user.getPassword());
        // hash加密数据，避免明文存储
        String hashUserName = hashPassword(user.getUsername());
        user.setUsername(hashUserName); // 将加密后的用户名设置回User对象
        String hashedPassword = hashPassword(user.getPassword());
        // 调service层方法，进行数据保存
        user.setPassword(hashedPassword); // 将加密后的密码设置回User对象

        System.out.println("加密后的用户名：" + user.getUsername());
        System.out.println("加密后的密码：" + user.getPassword());
        Result result = webService.login(user);

        // 登录成功，生成token并保存到Redis
        if ("200".equals(result.getCode())) { // 修正：字符串比较用equals，避免空指针风险
            String token = jwtUtils.generateToken(user); // 使用注入的jwtUtils
            System.out.println("token：" + token);
            JedisUtil jedis = JedisUtil.getInstance();
            jedis.setup();
            // 注意：setEx的第三个参数是秒（不是毫秒），与JWT的过期时间保持一致（3600秒=1小时）
            jedis.setEx(user.getUsername(), token, 3600);
            jedis.close();

            return Result.success(token);
        } else {
            // 登录失败，返回错误信息
            return Result.error("500", result.getMsg());
        }
    }

    // 注册接口
    @PostMapping("/registration")
    public Result registration(@RequestBody User user) {
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        // hash加密数据，避免明文存储
        String hashUserName = hashPassword(user.getUsername());
        user.setUsername(hashUserName); // 将加密后的用户名设置回User对象
        String hashedPassword = hashPassword(user.getPassword());
        // 调service层方法，进行数据保存
        user.setPassword(hashedPassword); // 将加密后的密码设置回User对象
        System.out.println("加密后的用户名：" + user.getUsername());
        System.out.println("加密后的密码：" + user.getPassword());
        try{
            
            webService.registration(user);
            // 注册成功，生成token并保存到Redis
            String token = jwtUtils.generateToken(user); // 使用注入的jwtUtils
            System.out.println("token：" + token);
            JedisUtil jedis = JedisUtil.getInstance();
            jedis.setup();
            jedis.setEx(user.getUsername(), token, 3600);
            jedis.close();
            return Result.success("注册成功");
        }catch(CustomException e){
            System.out.println("抛出错误"+e);
            return Result.error("500", e.getMsg());
            
        }  
    }

    /**
     * 验证token有效性接口
     * 
     * @param token 前端传递的JWT令牌
     * @return 验证结果（统一使用Result格式，与其他接口保持一致）
     */
    @GetMapping("/validateToken")
    public Result validateToken(@RequestParam String token) {
        // 1. 检查token是否为空
        if (token == null || token.trim().isEmpty()) {
            return Result.error("401", "token不能为空");
        }

        // 2. 验证token格式和签名有效性
        if (!jwtUtils.validateToken(token)) {
            return Result.error("401", "无效的token（已过期、被篡改或格式错误）");
        }

        // 3. 从token中提取用户名
        String username = jwtUtils.extractUsername(token);
        if (username == null || username.trim().isEmpty()) {
            return Result.error("401", "token中未包含有效的用户名");
        }

        // 4. 验证Redis中是否存在该token
        JedisUtil jedis = JedisUtil.getInstance();
        if (!jedis.setup()) { // 初始化Redis连接
            return Result.error("500", "Redis连接失败，无法验证token");
        }

        try {
            // 检查Redis中用户名对应的token是否与当前token一致
            if (!jedis.exits(username, token)) {
                return Result.error("401", "token未注册或已被注销");
            }

            // 5. 验证通过，返回详细信息
            Date expirationDate = jwtUtils.extractExpiration(token);
            long remainingMinutes = (expirationDate.getTime() - System.currentTimeMillis()) / (60 * 1000);

            return Result.success(String.format(
                    "token验证通过！用户名：%s，剩余有效时间：约%d分钟",
                    username,
                    remainingMinutes));
        } finally {
            // 确保Redis连接关闭，避免资源泄漏
            jedis.close();
        }
    }
}