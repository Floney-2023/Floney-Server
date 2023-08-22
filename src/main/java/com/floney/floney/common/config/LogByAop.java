package com.floney.floney.common.config;

import com.floney.floney.common.exception.book.NotFoundBookException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class LogByAop {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Pointcut("execution(* com.floney.floney.*.controller..*.*(..))")
    private void cutAllRequest() {
    }

    @Before("cutAllRequest()")
    public void beforeRequest(JoinPoint joinPoint) {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String method = request.getMethod();
        String path = request.getRequestURI();

        List<String> data = Arrays.stream(joinPoint.getArgs())
            .map(Object::toString)
            .toList();

        logger.info("[Access Log] " + method + " " + path + " [Data] " + data);
    }

}
