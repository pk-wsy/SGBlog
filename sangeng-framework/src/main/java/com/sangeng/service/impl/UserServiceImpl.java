package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.UserMapper;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-09-17 09:03:46
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public ResponseResult userInfo() {

        //从securityContextHolder中取出userId(用我们的工具类)
        Long userId = SecurityUtils.getUserId();
        //根据id查询用户信息
        User user = userService.getById(userId);
        //封装至vo对象
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        //前端传来的用户信息中，包括用户id、用户昵称、用户邮箱、用户性别、用户头像的地址
        //因此根据唯一性标识字段——用户id进行相关修改即可
        //但是这么做有可能有风险，恶意请求可以跳过前端直接访问后端接口，并携带密码等参数，这样这个方法可能就将用户对应的密码也改掉了
        //最好是使用update()方法中传入一个wrapper对象，可以实现对某几个字段的精确修改，防止恶意请求改我们不想让修改的字段
        //这里是为了测试，使用此方法也可
        updateById(user);
        //成功则返回我们的统一响应格式，无数据返回
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //实现对于前端传来的信息校验（后端也需要进行校验，防止恶意请求直接打到后端接口） 这里只进行简单校验
        // 用户名、昵称、密码、邮箱不能为空
        if (!StringUtils.hasText(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //对于用户名、昵称、邮箱的唯一性检验
        if(userNameExists(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExists(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        if(emailExists(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        //校验通过后，注意我们在SpringSecurity中使用的编解码器要求在数据库中存储的密码应当是密文，这样在登录进行校验时才会将前端传来的密码明文和数据库存储的密码密文进行匹配
        //因此我们需要在Security配置类中配置的编码器，来对现在的铭文密码进行加密
        String password = passwordEncoder.encode(user.getPassword());
        //加密后，要将user对象的属性值更新为加密后的密码
        user.setPassword(password);

        // 最后将用户信息存储到数据库中
        save(user);
        //全部成功执行后，返回统一响应格式，由于是新增用户，因此没有data数据返回
        return ResponseResult.okResult();
    }

    public boolean userNameExists(String userName) {
        //创建过滤对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //查询是否有相同用户名的数据
        queryWrapper.eq(User::getUserName, userName);
        int count = count(queryWrapper);
        return count > 0;
    }

    public boolean nickNameExists(String nickName) {
        //创建过滤对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //查询是否有相同昵称的数据
        queryWrapper.eq(User::getNickName, nickName);
        int count = count(queryWrapper);
        return count > 0;
    }

    public boolean emailExists(String email) {
        //创建过滤对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //查询是否有相同邮箱的数据
        queryWrapper.eq(User::getEmail, email);
        int count = count(queryWrapper);
        return count > 0;

    }
}
