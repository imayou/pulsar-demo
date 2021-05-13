package com.ayou.pulsardemo.infrastructure.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ysy
 * @blame ysy
 * @date 2020-01-11
 */
@Aspect
public class PulsarListenerAspect {
    //@Before("@annotation(com.ayou.study.december.annotation.PulsarListener)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
