package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.Menu;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.service.MenuService;
import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-09-23 08:00:07
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserId(Long id) {
        // 根据id进行判断
        // 如果id为1（默认是管理员），我们封装在SecurityUtils类中方法
        if(SecurityUtils.isAdmin()){
            //则查出所有符合基本过滤条件的权限
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            //目录属性是M、C
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU_TYPE_C,SystemConstants.MENU_TYPE_F);
            // 目录状态为正常
            queryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            //根据过滤条件查询相关记录
            List<Menu> menuList = list(queryWrapper);
            //取出perms字段,收集到一个list集合中并返回
            return menuList.stream()
                    .map(Menu::getPerms).collect(Collectors.toList());
        }

        //如果不是管理员，则需要查询该id对应的用户拥有的符合基本过滤条件的所有权限
        return getBaseMapper().selectPermsByUserId(id);
    }


    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long id) {
        List<Menu> menuList = new ArrayList<>();
        // 先判断用户是否为超级管理员
        if(SecurityUtils.isAdmin()){
            //如果是超级管理员，默认具有基本过滤条件后的所有权限
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            //基本过滤条件是权限为"M"、"C"，menu状态为0
            queryWrapper.in(Menu::getMenuType,SystemConstants.MENU_TYPE_M,SystemConstants.MENU_TYPE_C);
            queryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            //根据parent_id和order_num两个字段进行排序
            queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
            //查询出符合条件的记录，赋值给集合
            menuList = list(queryWrapper);
        }else{
            //如果不是超级管理员，则需要找出该id下符合基本过滤条件的权限
            menuList = getBaseMapper().selectRouterMenuTreeByUserId(id);
        }

        //把MenuList集合中的每一个Menu对象的children属性进行校验并赋值
        List<Menu> menuTree = builderMenuTree(menuList, 0L);
        return menuTree;
    }

    /**
     * 实现对于得到的包含menu对象的集合中的children属性进行依次赋值（如果有子权限则添加进children，没有则不赋值）
     * @param menuList
     * @param parentId
     * @return
     */
    private List<Menu> builderMenuTree(List<Menu> menuList, long parentId) {
        //转为stream流进行操作
        List<Menu> menuTree = menuList.stream()
                //找到父节点是0的menu权限对象（经过这个判断后，剩下的权限都是第一级权限对象）
                .filter(menu -> menu.getParentId().equals(parentId))
                //一级权限的children属性可能有值，对他们的children属性进行赋值
                //找到每个权限的children属性的流程我们再封装到一个方法中
                .map(menu -> menu.setChildren(getChildren(menu, menuList)))
                //经过此步之后，所有的权限如果有子权限，则已经装填入children中，完成了权限的父子层级构建
                //于是将这些一级权限对象重新收集起来（他们是父子关系的第一层），返回
                .collect(Collectors.toList());

        return menuTree;
    }

    /**
     * 实现对于一个权限对象，在权限集合对象中查找它的子权限，如果有子权限则装填入children属性中，没有子权限则不装填
     * @param menu
     * @param menuList
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menuList) {
        //转化为流对象操作
        List<Menu> childrenList = menuList.stream()
                //如果要找menu对象的子权限，那么意味着menuList中的menu对象的父id与menu的id相等
                //所以经过此条件过滤的权限对象，就是传入的menu对象的子权限对象集合
                .filter(m -> m.getParentId().equals(menu.getId()))
                //对于这些权限，他们如果在权限集合中有自己的子权限，也应当重复此工作，这里使用递归的思想实现
                //对于自己的children属性进行赋值
                .map(m -> m.setChildren(getChildren(m, menuList)))
                //将这些对象收集起来返回
                .collect(Collectors.toList());
        return childrenList;
    }
}

