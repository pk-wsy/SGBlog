package com.sangeng.controller;

import com.sangeng.annotation.SystemLog;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 用于显示用户个人信息的接口
     */
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    /**
     * 实现更新用户个人信息的接口
     * @param user 前端传来的用户相关信息（用于更改）
     * @return 统一响应格式
     */
    @PutMapping("/userInfo")
    //这里的自定义注解用于测试我们的AOP功能
    @SystemLog(businessName = "更新用户信息")
    public ResponseResult updateUserInfo(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    /**
     * 实现用户注册的需求（在sys_user表中新增一条记录）
     * @param user 封装了前端传来的用户名、密码、邮箱、昵称的用户对象
     * @return 返回统一响应格式即可
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }
}
