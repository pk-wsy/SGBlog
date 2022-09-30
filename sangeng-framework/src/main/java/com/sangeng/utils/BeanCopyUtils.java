package com.sangeng.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    /**
     * 封装了将一个实体类对象通过bean拷贝到一个VO对象的过程
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T copyBean(Object source, Class<T> clazz){
        //根据目标类的class对象，通过反射创建出该类的实例
        T t = null;
        try {
            Constructor<T> constructor = clazz.getConstructor();
            t = constructor.newInstance();
        //调用beanutils的方法，实现从源对象到目标对象的bean拷贝
            BeanUtils.copyProperties(source,t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(t);
        return t;
    }

    public static <T,V> List<T> copyBeanList(List<V> source, Class<T> clazz){
        //使用stream流的方式
        //map()可以将接收到的实体类对象转换为VO对象
        //转换为一个个对象后，收集起来，变为list集合
        return source.stream().
                map(obj -> copyBean(obj, clazz)).collect(Collectors.toList());
    }
}
