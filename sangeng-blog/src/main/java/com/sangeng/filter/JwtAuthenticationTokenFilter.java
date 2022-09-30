package com.sangeng.filter;


import com.alibaba.fastjson.JSON;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import com.sangeng.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 实现自定义校验过滤器，前端如果携带token则无需进入登录功能
 * 定义过滤器之后，不要忘记配置给SpringSecurity配置类中，让它知道先走这个过滤器
 *
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中的token
        String token = httpServletRequest.getHeader("token");
        //判断获得的token是否为空
        if(!StringUtils.hasText(token)){
            //为空意味着没有token，需要让用户进行登录（放行的话就会有相关提示，并且不让他走本方法后面的逻辑）
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //如果不为空
        //我们要将token解析出用户id
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            //出现异常的话说明token有问题，或者token已经过期
            e.printStackTrace();
            //我们后台向前台的提示要符合统一响应格式，因此需要封装
            ResponseResult responseResult = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            //由于过滤器是在controller层之前，之间返回不会以json的格式展示在前端
            //因此可以使用我们定义的工具类来实现在过滤器中输出某些信息并以json的格式展示在前端页面
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(responseResult));
            return;
        }
        //如果解析json的过程没问题，则会获取到userId
        String userId = claims.getSubject();
        //根据userId和userId前缀从redis中取出用户信息
        LoginUser loginUser = redisCache.getCacheObject(SystemConstants.ID_PREFIX + userId);
        //判断loginUser是否为空
        if(Objects.isNull(loginUser)){
            //意味着redis中用户信息已经过期，需要重新登陆
            ResponseResult responseResult = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse,JSON.toJSONString(responseResult));
            return;
        }
        //将用户信息封装为认证信息
        //需要存入SecurityContextHolder中，供之后Security进行判断调用（判断如果这个Holder里有用户信息，就无需进行登录功能）
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //业务逻辑完成则放行
        //后续将检查holder中有没有用户信息，有的话不会进行认证（没有的话在前面就进行了放行，会自动调用认证方法）
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
