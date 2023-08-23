package com.example.backend.util;

import com.example.backend.util.security.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MdcAspect {

    @Around("execution(* com.example.backend.track.controller.*.*(..)) && args(.., userDetails)")
    public Object handleMdc(ProceedingJoinPoint joinPoint, UserDetailsImpl userDetails) throws Throwable {
        try {
            MDC.put("userId", userDetails.getUser().getUserId().toString());
            return joinPoint.proceed();  // 원래 메소드의 실행
        } finally {
            MDC.clear();  // 후처리 작업
        }
    }
}
