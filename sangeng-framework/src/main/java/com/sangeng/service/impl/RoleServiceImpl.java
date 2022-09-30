package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.Role;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.RoleService;
import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-09-23 07:57:02
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //根据id进行判断(controller传给service层的id就是从securityUtils工具类取出来的)
        //判断当前用户是否为管理员
        if(SecurityUtils.isAdmin()){
            //是管理员的话，默认具有“admin”的角色
            List<String> list = new ArrayList<>();
            list.add("admin");
            return list;
        }
        //如果不是管理员，则需要查询对应id下符合基本过滤条件的所有角色
        return getBaseMapper().selectRoleKeyByUserId(id);
    }
}

