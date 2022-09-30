package com.sangeng.aspect;

import com.alibaba.fastjson.JSON;
import com.sangeng.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AOP的配置类
 * 应当确保其在Spring容器中
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    /**
     * 在一个方法上加上@Pointcut注解，可以实现切入点的复用，之后只需要在包含增强逻辑方法的相关AOP注解的属性值中传入该方法，即可实现切入点的复用
     * 切入点@Pointcut注解的value值没有填写切入点表达式，而是自定义注解的形式，这样可以确保加上该注解的方法可以被增强，使用起来更加灵活
     */
    @Pointcut("@annotation(com.sangeng.annotation.SystemLog)")
    public void pt(){}

    /**
     * Around注解可以实现环绕通知，在原本方法的执行的前后都可以执行增强方法
     * @param joinPoint 封装了被增强方法的相关信息
     * @return 要把原本的方法（被增强方法）的返回值进行返回
     */
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint)throws Throwable{
        Object ret;

        try {
            handleBefore(joinPoint);
            //执行该被增强方法自己的逻辑代码
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());

        }
        return ret;


    }

    /**
     * 封装了打印的增强方法的逻辑（执行在被增强方法之前）
     * @param joinPoint 封装了被增强方法的信息，用于取到一些参数
     */
    private void handleBefore(ProceedingJoinPoint joinPoint){
        //SpringBoot帮我们把每次请求都封装在了RequestContextHolder中，要去到Request相关的信息，需要从里面取
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //增强方法的逻辑（在被增强方法前置性）
        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}",request.getRequestURL());
        // 打印描述信息
        //这个描述信息指的是自己定义的@SystemLog注解中的businessName属性中的值，我们可以在要用日志功能增强的方法上加上这个注解
        //通过指明businessName的值来对这个方法进行描述，这样在控制台中也会随着AOP功能的实现，输出出来
        //取到systemLog的步骤我们封装在一个方法中
        //获取到注解对象就可以直接获取到它的属性值
        log.info("BusinessName   : {}", getSystemLog(joinPoint).businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), ((MethodSignature) joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());
        // 打印请求入参
        //由于返回值是Object对象数组，因此可以用JSON进行序列化，以直接显示相关内容
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    /**
     * 封装了打印的增强方法的逻辑（执行在被增强方法之后）
     * @param ret 被增强方法的返回值
     */
    private void handleAfter(Object ret){
        // 打印出参
        // ret是个对象，用JSON进行序列化输出，具有可读性
        log.info("Response       : {}", ret);

    }
    /**
     * 得到@SystemLog的注解
     * @param joinPoint 封装了方法的信息（该方法标注了SystemLog的注解）
     * @return SystemLog对象
     */
    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint){
        //获取方法Method对象
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        //获取方法上的注解(要指明注解的类型，以免该方法有多个注解获取到其他注解)
        SystemLog systemLog = method.getAnnotation(SystemLog.class);
        return systemLog;
    }


}
