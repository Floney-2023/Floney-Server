package com.floney.floney.common.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Aspect
@Slf4j
@Component
public class LogByAop {

    @Pointcut("execution(* com.floney.floney.*.controller..*.*(..))")
    private void cutAllRequest() {
    }

    @Before("cutAllRequest()")
    public void beforeRequest(final JoinPoint joinPoint) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        final String method = request.getMethod();
        final String path = request.getRequestURI();
        final String ipAddress = request.getRemoteAddr();

        final List<String> data = Arrays.stream(joinPoint.getArgs())
            .map(Object::toString)
            .toList();

        log.info("[ACCESS LOG] {} {} [REQUEST DATA] {} [IP] {}", method, path, data, ipAddress);
    }

}
