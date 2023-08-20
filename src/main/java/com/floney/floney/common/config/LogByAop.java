package com.floney.floney.common.config;

import com.floney.floney.common.exception.book.NotFoundBookException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogByAop {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Pointcut("execution(* com.floney.floney.*..*.*(..))")
    private void cutAllRequest() {
    }

    @Before("cutAllRequest()")
    public void beforeRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("METHOD : " + className + "." + methodName);
    }

}
