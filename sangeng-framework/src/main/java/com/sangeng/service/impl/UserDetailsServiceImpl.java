package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

//这个注解不能忘！！！！！！（否则会出现堆栈溢出异常）（authentication认证过程中需要调用这个接口实现类的方法，因此得把这个类对象放到容器中）
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MenuMapper menuMapper;
    /**
     * 前面调用了认证方法，密码的校验有springsecurity实现，我们想要用户的认证是通过查询数据库实现（SpringSecurity默认从内存）
     * 因此需要重写此方法，之后调用链会调用该方法
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //判断传来的用户名是否为空，如果为空则需要抛出异常
        if(!StringUtils.hasText(s)){
            throw new RuntimeException("请输入用户名！");
        }
        //走到这说明前端传来了用户名
        //需要根据传来的用户名在数据库中查询是否存在
        //创建过滤条件对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,s).eq(User::getStatus, SystemConstants.USER_STATUS_NORMAL);
        //根据过滤条件，得到查询的用户对象
        User user = userMapper.selectOne(queryWrapper);
        //需要判断此用户对象是否为空
        if(user == null){
            throw new RuntimeException("账号不存在或账号已停用！");
        }
        //TODO 查询权限信息封装
        //如果是后台用户，我们才查询相关权限，并封装到loginuser对象中；如果不是后台用户，不用查询权限
        //判断该用户是否为后台用户(通过user表中的type属性值来进行标识)
        if(user.getType().equals(SystemConstants.ADMIN_USER)){
            //查出该用户的权限
            List<String> list = menuMapper.selectPermsByUserId(user.getId());
            //封装至LoginUser对象中，并返回
            return new LoginUser(user,list);
        }
        //走到这说明是前台用户，且用户名在数据库中存在，则需要封装为UserDetails的实现类给予前面响应
        //之前创建了UserDetails的实现类（LoginUser），我们需要把查出来的用户封装在此对象中
        return new LoginUser(user,null);
    }
}
