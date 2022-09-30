package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.AdminUserInfoVo;
import com.sangeng.domain.vo.RoutersVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.BlogLoginService;
import com.sangeng.service.LoginService;
import com.sangeng.service.MenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping()
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;
    /**
     * 后台实现登录功能
     * 后台的登录功能必须要先完成，才能进行后续工作
     * @param user
     * @return
     */
    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //如果没有传来用户名，则提示需要传来用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        //如果传了可以继续进行处理
        return loginService.login(user);
    }

    /**
     * 实现后台用户在登录后，查询出关于该用户所对应的权限、角色和用户信息，用于权限控制
     * @return
     */
    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //先得到该用户对应的id
        Long id = SecurityUtils.getUserId();
        //根据id去查询相关内容
        //调用menuService查询id对应的所有权限，并取出perms字段
        List<String> perms = menuService.selectPermsByUserId(id);

        //调用RoleService去查询id所对应的所有角色，并取出role_key字段
        List<String> roles = roleService.selectRoleKeyByUserId(id);

        //取出该id的用户信息
        User user = SecurityUtils.getLoginUser().getUser();
        //拷贝至UserInfoVo对象中
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //封装至vo对象中，返回前台
        return ResponseResult.okResult(new AdminUserInfoVo(perms,roles,userInfoVo));

    }

    /**
     * 协助前端实现动态路由的功能，即具有不同权限的用户在前端的后台展示页面不同
     * 需要传递给前端当前用户所具有的权限信息，还要体现出权限的父子层级关系
     * @return
     */
   @GetMapping("/getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        //查询当前用户的id
       Long id = SecurityUtils.getUserId();

       //根据当前用户id，查询其所具有的权限信息，并对具有的权限信息通过children字段表示父子层级关系
       List<Menu> menus = menuService.selectRouterMenuTreeByUserId(id);

       return ResponseResult.okResult(new RoutersVo(menus));


    }

    /**
     * 实现退出登录的功能
     * @return
     */
    @PostMapping("/user/logout")
    public ResponseResult logout(){
       return loginService.logout();
    }


}
