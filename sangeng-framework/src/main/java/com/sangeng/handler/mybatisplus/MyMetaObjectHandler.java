package com.sangeng.handler.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sangeng.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MP的自动填充配置实现类
 * 用于定义字段的自动填充规则
 * 定义哪些字段使用这个自动填充规则，则需要去对应字段的上方加上@TableField注解，并根据要求选择fill属性的值
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 字段添加的规则，在插入数据时会自动执行该方法
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;

        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            //注册用户的时候会走到catch块中
            //因为注册的时候也是插入数据的操作，然而还没有用户id
            e.printStackTrace();
            userId = -1L;//表示是自己创建
        }
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("createBy",userId,metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
        this.setFieldValByName("updateBy",userId,metaObject);

    }

    /**
     * 字段修改的规则，在更新时会自动执行该方法
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime",new Date(),metaObject);
        this.setFieldValByName("updateBy",SecurityUtils.getUserId(),metaObject);
    }
}
