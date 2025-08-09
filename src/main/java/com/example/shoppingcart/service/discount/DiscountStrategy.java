package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ShoppingCart;

import java.math.BigDecimal;

/**
 * Strategy interface for applying different types of discounts.
 * Each implementation will handle a specific discount calculation logic.
 */
public interface DiscountStrategy {

    /**
     * Applies a discount to a shopping cart based on a specific rule.
     *
     * @param cart The shopping cart to apply the discount to.
     * @param rule The discount rule containing the parameters for the calculation.
     * @return The calculated discount amount in pence.
     */
    BigDecimal apply(ShoppingCart cart, DiscountRule rule);
}