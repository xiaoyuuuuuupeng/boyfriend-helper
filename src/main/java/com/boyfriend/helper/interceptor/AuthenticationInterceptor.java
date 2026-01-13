package com.boyfriend.helper.interceptor;

import com.boyfriend.helper.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_PREFIX = "auth:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(token)) {
            response.setStatus(401);
            return false;
        }

        String userIdStr = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (!StringUtils.hasText(userIdStr)) {
            response.setStatus(401);
            return false;
        }

        UserContext.setUserId(Long.valueOf(userIdStr));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
