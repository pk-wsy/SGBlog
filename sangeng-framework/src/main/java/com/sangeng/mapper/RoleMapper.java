package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-09-23 07:56:59
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 对于非管理员用户，查询对应用户id下所具有的所有角色（role），并封装每条记录的role_key在集合中返回
     * @param id
     * @return
     */
    List<String> selectRoleKeyByUserId(Long id);
}

