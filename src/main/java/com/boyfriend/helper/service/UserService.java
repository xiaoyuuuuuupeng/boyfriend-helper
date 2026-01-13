package com.boyfriend.helper.service;

import com.boyfriend.helper.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {

    void sendVerificationCode(String phone);

    Map<String, Object> login(String phone, String code);

    User getUserInfo(Long userId);

    void kickUser(Long userId);

    void logout(String token);
}
