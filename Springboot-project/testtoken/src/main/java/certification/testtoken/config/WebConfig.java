package certification.testtoken.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import certification.testtoken.util.TokenAuthInterceptor;

/**
 * Web配置类
 * 作用：注册拦截器，配置拦截规则（哪些请求需要验证Token，哪些不需要）
 * 实现WebMvcConfigurer接口，重写addInterceptors方法
 */
@Configuration // 标记为配置类，Spring启动时会加载此类
public class WebConfig implements WebMvcConfigurer {

    // 注入自定义的Token拦截器（已通过@Component标记为组件）
    @Autowired
    private TokenAuthInterceptor tokenAuthInterceptor;

    /**
     * 注册拦截器并配置拦截规则
     * @param registry 拦截器注册表，用于添加拦截器和设置规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册TokenAuthInterceptor拦截器
        registry.addInterceptor(tokenAuthInterceptor)
                .addPathPatterns("/**") // 拦截所有请求（/**表示所有路径）
                .excludePathPatterns("/login")
                .excludePathPatterns("/registration")
                .excludePathPatterns("/validateToken"); // 排除登录接口（登录时还没有Token，无需验证）
                // 可继续添加其他排除路径，如注册接口：.excludePathPatterns("/login", "/register")
    }
}