package net.sonerapp.jwtauth.core.aspects;

import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAspect {

    private Logger log = LoggerFactory.getLogger(LoggerAspect.class.getName());

    @Around("execution(* net.sonerapp.jwtauth.core.service.impl..*.*(..)) || execution(* net.sonerapp.jwtauth.core.event..*.*(..))")
    public Object logLogic(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(joinPoint.getSignature().toString() + " method execution start");
        Instant start = Instant.now();
        Object result = joinPoint.proceed();
        Instant end = Instant.now();
        long executionDuration = Duration.between(start, end).toMillis();
        log.info("Time took to execute the method: " + executionDuration + "ms");
        log.info(joinPoint.getSignature().toString() + " method execution end");
        return result;
    }

    @AfterThrowing(value = "execution(* net.sonerapp.jwtauth.core..*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.warn(joinPoint.getSignature() + " An exception is thrown. Reason: " + ex.getMessage());

    }

}
