package com.mtalk.utils;

import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.mtalk.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static com.mtalk.utils.constant.RedisConstant.USER_CACHE_KEY;
import static com.mtalk.utils.constant.RedisConstant.USER_CACHE_TIME;

@CrossOrigin
public class LoginInterceptor implements HandlerInterceptor {
    //这不是spring对象，是自己创建的类，不能加@Resource注解或@Autowired注解
    private StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
    //注入StringRedisTemplate
    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @CrossOrigin
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        System.out.println("token: " + token);
        if(token == null){
            return true;
        }
        if(token.isBlank()){
            return false;
        }
        String userJson = stringRedisTemplate.opsForValue().get(USER_CACHE_KEY + token);
        if(token.isBlank()){
            return false;
        }
        User user = JSONUtil.toBean(userJson, User.class);
        // 保存在ThreadLocal中
        LocalUser.setLocalUser(user);
        // 刷新Redis有效期 7天
        stringRedisTemplate.expire(USER_CACHE_KEY + token,USER_CACHE_TIME, TimeUnit.DAYS);
        return true;
    }


}
