package com.example.shoppingcart.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PriceCalculationAspect {
    private static final Logger log = LoggerFactory.getLogger(PriceCalculationAspect.class);

    @Around("execution(* com.example.shoppingcart.service.PriceCalculationService.calculateTotal(..))")
    public Object logPriceCalculation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Price calculation completed in {}ms. Result: {}",
                duration, result);

            return result;
        } catch (Exception e) {
            log.error("Error during price calculation: {}", e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.example.shoppingcart.service.impl.BasketServiceImpl.checkout(..))")
    public Object monitorCheckout(ProceedingJoinPoint joinPoint) throws Throwable {
        String cartId = (String) joinPoint.getArgs()[0];
        log.info("Starting checkout process for cart: {}", cartId);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Checkout completed for cart {} in {}ms", cartId, duration);
            return result;
        } catch (Exception e) {
            log.error("Checkout failed for cart {}: {}", cartId, e.getMessage());
            throw e;
        }
    }
}
