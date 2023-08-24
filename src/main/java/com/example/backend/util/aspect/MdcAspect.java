package com.example.backend.util.aspect;

import com.example.backend.util.security.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MdcAspect {

    @Around("execution(* com.example.backend.*.controller.*.*(..))")
    public Object handleMdc(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if ("trackId".equals(parameterNames[i]) && args[i] instanceof String) {
                String trackId = (String) args[i];
                MDC.put("trackId", trackId);
            }
            if (args[i] instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) args[i];
                MDC.put("userId", userDetails.getUser().getUserId().toString());
            }
        }

        try {
            return joinPoint.proceed();
        } finally {
            MDC.clear();
        }
    }
}