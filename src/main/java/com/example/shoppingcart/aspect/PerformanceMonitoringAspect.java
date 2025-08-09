package com.example.shoppingcart.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceMonitoringAspect {
    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);
    private final MeterRegistry meterRegistry;

    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCacheHits(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        boolean cacheHit = false;
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            cacheHit = result != null;
            return result;
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            meterRegistry.counter("cache.access",
                "method", methodName,
                "result", cacheHit ? "hit" : "miss").increment();

            meterRegistry.timer("method.timing",
                "method", methodName).record(duration, TimeUnit.MILLISECONDS);

            if (duration > 1000) {
                log.warn("Slow method execution detected: {} took {}ms", methodName, duration);
            }
        }
    }

    @Around("execution(* com.example.shoppingcart.service.impl.CheckoutServiceImpl.*(..))")
    public Object monitorBasketOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().getName();

        meterRegistry.counter("basket.operations",
            "operation", operation).increment();

        return joinPoint.proceed();
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object monitorTransactions(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.nanoTime();

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            meterRegistry.counter("transaction.failures",
                "method", methodName,
                "exception", e.getClass().getSimpleName()).increment();
            throw e;
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            meterRegistry.timer("transaction.timing",
                "method", methodName).record(duration, TimeUnit.MILLISECONDS);
        }
    }
}
