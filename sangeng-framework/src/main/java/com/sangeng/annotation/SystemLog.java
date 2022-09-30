package com.sangeng.annotation;

import lombok.experimental.Accessors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解，后续加上这个注解的方法都会通过AOP被增强，用日志记录相关信息
 * 使用这个就可以不用使用切入点表达式了，也会更加灵活方便
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemLog {
    //加上一个属性，用于后续将此注解标注在方法上时，设置该方法的相关说明
    String businessName();
}
