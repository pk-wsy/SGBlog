package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2022-09-23 08:00:04
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 对于非管理员的用户，查询用户id下对应的权限的perms
     * @param id
     * @return
     */
    List<String> selectPermsByUserId(Long id);

    /**
     * 对于非管理员的用户，查询用户id下对应的权限信息，用于实现动态路由的需求
     * @param id
     * @return
     */
    List<Menu> selectRouterMenuTreeByUserId(Long id);
}

