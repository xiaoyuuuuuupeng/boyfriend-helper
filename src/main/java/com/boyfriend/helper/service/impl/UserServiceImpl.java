package com.boyfriend.helper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boyfriend.helper.entity.User;
import com.boyfriend.helper.mapper.UserMapper;
import com.boyfriend.helper.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "auth:code:";
    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_TOKEN_PREFIX = "auth:user:token:";
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final long TOKEN_EXPIRE_DAYS = 30;

    @Override
    public void sendVerificationCode(String phone) {
        // Mock implementation
        String code = "123456"; // Fixed code for now
        String key = CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("Sent verification code {} to phone {}", code, phone);
    }

    @Override
    public Map<String, Object> login(String phone, String code) {
        // 1. Verify code
        String key = CODE_PREFIX + phone;
        String cachedCode = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(cachedCode) || !cachedCode.equals(code)) {
            throw new RuntimeException("Invalid or expired verification code");
        }
        
        // Remove code after usage
        redisTemplate.delete(key);

        // 2. Find or Create User
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("User_" + phone.substring(phone.length() - 4));
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            this.save(user);
        }

        // 3. Generate Token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 4. Handle existing session (Optional: Kick previous session? Or allow multiple?)
        // Implementation: Single session enforcement (kick previous)
        String userTokenKey = USER_TOKEN_PREFIX + user.getId();
        String oldToken = redisTemplate.opsForValue().get(userTokenKey);
        if (StringUtils.hasText(oldToken)) {
            redisTemplate.delete(TOKEN_PREFIX + oldToken);
        }

        // 5. Store Token in Redis
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, String.valueOf(user.getId()), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(userTokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getUserInfo(Long userId) {
        return this.getById(userId);
    }

    @Override
    public void kickUser(Long userId) {
        String userTokenKey = USER_TOKEN_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(userTokenKey);
        if (StringUtils.hasText(token)) {
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(userTokenKey);
            log.info("Kicked user {}", userId);
        }
    }

    @Override
    public void logout(String token) {
        String userIdStr = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (StringUtils.hasText(userIdStr)) {
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(USER_TOKEN_PREFIX + userIdStr);
        }
    }
}
