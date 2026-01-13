package com.boyfriend.helper.controller;

import com.boyfriend.helper.common.Result;
import com.boyfriend.helper.entity.User;
import com.boyfriend.helper.service.UserService;
import com.boyfriend.helper.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth/code")
    public Result<Void> sendCode(@RequestParam String phone) {
        userService.sendVerificationCode(phone);
        return Result.success();
    }

    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestParam String phone, @RequestParam String code) {
        return Result.success(userService.login(phone, code));
    }

    @GetMapping("/user/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @PostMapping("/admin/kick")
    public Result<Void> kickUser(@RequestParam Long userId) {
        userService.kickUser(userId);
        return Result.success();
    }
    
    @PostMapping("/auth/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return Result.success();
    }
}
