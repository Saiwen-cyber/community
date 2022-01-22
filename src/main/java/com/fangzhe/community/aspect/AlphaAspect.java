package com.fangzhe.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author fang
 */
//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.fangzhe.community.service.*.*(..))")
    public void pointcut(){
        System.out.println("before");
    }

    @Before("pointcut()")
    public void  before(){
        System.out.println("before");
    }

    @AfterReturning("pointcut()")
    public void  afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        //调用目标组件方法
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
