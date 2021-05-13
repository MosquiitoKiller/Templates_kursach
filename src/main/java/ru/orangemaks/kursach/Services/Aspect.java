package ru.orangemaks.kursach.Services;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {
    @Pointcut("within(ru.orangemaks.kursach.Services.*)")
    public void allServiceMethods(){}
    @Around("allServiceMethods()")
    public Object timeLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        long startTime = System.currentTimeMillis();
        Object res = proceedingJoinPoint.proceed();
        long finishTime = System.currentTimeMillis();

        log.info("Method "+methodName+" in "+className+" working "+(finishTime-startTime)+" ms");
        return res;
    }
}