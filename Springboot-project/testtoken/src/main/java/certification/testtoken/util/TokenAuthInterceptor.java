package certification.testtoken.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JedisUtil jedisUtil;


    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception{

        //1.从请求头中获取Token
        String authHeader = request.getHeader("Authorization");

        //2.判断Token格式
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            response.setStatus(401);
            response.getWriter().write("need Bearer Token");
            return false;
        }

        //3.去掉前缀
        String token = authHeader.substring(7);

        //4.验证token合法性
        if(!jwtUtils.validateToken(token)){
            response.setStatus(401);
            response.getWriter().write("invalid token");
            return false;
        }

        //5.从token中获取用户信息，用户redis验证
        String username  = jwtUtils.extractUsername(token);
        if(username==null){
            response.setStatus(401);
            response.getWriter().write("username not found");
            return false;
        }

        //6.验证redis中是否有对应token
        if(!jedisUtil.exits(username, token)){
            response.setStatus(401);
            response.getWriter().write("Token not found");
        }
        return true;
    }

}
