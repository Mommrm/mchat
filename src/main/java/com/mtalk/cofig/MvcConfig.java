package com.mtalk.cofig;

import com.mtalk.utils.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")      //允许跨域访问的路径
                .allowedOriginPatterns("*")              //允许跨域访问的源
                .allowedMethods("POST","GET","PUT","OPTIONS","DELETE")  //运行请求方法
                .maxAge(168000)             //预检间隔时间
                .allowedHeaders("*")        //允许头部设置
                .allowCredentials(true);  //是否发送Cookie
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate)).excludePathPatterns(
                    "/user/land",
                    "/user/register"
        );
    }
}
