package com.sangeng.service.impl;

import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.BlogUserLoginVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.service.BlogLoginService;

import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service("blogLoginService")
public class BlogLoginServiceImpl implements BlogLoginService {
    /**
     * 用于调用认证方法的对象（需要在配置类中配置）
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        //认证前，需要创建被认证的对象，这里需要传递一个Authentication接口的实现类，我们接收到的参数是用户名和密码，因此我们创建该实现类
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        //将认证对象传入到方法参数中，用于调用后续认证工序
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //判断认证后对象是否为空
        if(Objects.isNull(authenticate)){
            //如果为空，意味着用户名或密码的校验未通过
            throw new RuntimeException("用户名或密码不正确！");
        }
        //如果查到了用户名，我们需要取到里面的用户信息
        //先取出认证对象的主体（在调用链中传递的UserDetails实现类）
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //取出用户的id
        String id = loginUser.getUser().getId().toString();
        //调用提供的JWT工具类来实现根据用户id，生成token
        String token = JwtUtil.createJWT(id);
        //调用自己封装好的redis工具类，以id和一定前缀为key,用户信息为value，存入到redis缓存中
        //以实现下一次登录时可以解析token取到id，进而从redis中取到对应的用户信息
        redisCache.setCacheObject(SystemConstants.ID_PREFIX+id,loginUser);
        //将用户信息封装至vo对象中
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        //将userInfoVo中的userInfo和要一起返还给前端的token封装在一个对象中，因此还需要创建一个vo对象
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(token,userInfoVo);
        //封装为统一响应格式，返回至前端
        return ResponseResult.okResult(blogUserLoginVo);
    }


    @Override
    public ResponseResult logout() {
        //由于是登录状态，因此从SecurityContextHolder中取出用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //从认证对象中取到userId
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUser().getId();
        //删除redis中数据
        redisCache.deleteObject(SystemConstants.ID_PREFIX+id);
        return ResponseResult.okResult();
    }
}
