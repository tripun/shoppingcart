package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.service.DiscountService;
import com.example.shoppingcart.service.PriceCalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of the price calculation service.
 * This service is responsible for orchestrating the calculation of all monetary
 * values in a shopping cart, including subtotal, discounts, and the final total.
 */
@Service
@RequiredArgsConstructor
public class PriceCalculationServiceImpl implements PriceCalculationService {

    private static final Logger log = LoggerFactory.getLogger(PriceCalculationServiceImpl.class);

    private final DiscountService discountService;

    @Override
    public void calculateCartTotals(ShoppingCart cart) {
        log.debug("Calculating totals for cartId: {}", cart.getCartId());

        // 1. Calculate Subtotal (sum of all item prices before discounts)
        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setSubtotal(subtotal);
        log.debug("Calculated subtotal for cart {}: {}", cart.getCartId(), subtotal);

        // 2. Calculate Discounts by delegating to the DiscountService
        BigDecimal totalDiscount = discountService.calculateTotalDiscount(cart);
        cart.setTotalDiscount(totalDiscount);
        log.debug("Calculated total discount for cart {}: {}", cart.getCartId(), totalDiscount);

        // 3. Calculate Final Total and ensure it's not negative
        BigDecimal finalTotal = subtotal.subtract(totalDiscount).max(BigDecimal.ZERO);
        cart.setTotal(finalTotal);
        log.info("Final calculated total for cart {}: {}", cart.getCartId(), finalTotal);
    }
}
