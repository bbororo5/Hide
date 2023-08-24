package com.example.backend.util.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("execution(* com.example.backend.*.controller.*.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis(); // 시작 시간
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis(); // 종료 시간
            String methodName = joinPoint.getSignature().toShortString();
            long duration = endTime - startTime;
            log.info("{} took {}ms", methodName, duration); // 로그 출력
        }
    }
}
