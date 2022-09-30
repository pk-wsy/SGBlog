package com.sangeng.service.impl;

import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("ps")
//自定义权限校验类
public class PermissionService {

    /**
     * 判断当前用户是否具有某个permission
     * @param permission
     * @return
     */
    public boolean hasPermission(String permission){
        //如果该后台用户是超级管理员，则默认什么权限都有，直接返回true
        if(SecurityUtils.isAdmin()){
            return true;
        }
        //如果不是超级管理员，是普通的后台管理员，则获取当前用户所具有的权限列表，并判断是否包含传入的权限参数
        List<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions.contains(permission);
    }
}
