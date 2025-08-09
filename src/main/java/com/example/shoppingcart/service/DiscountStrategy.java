package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CheckoutResponse;
import com.example.shoppingcart.model.dynamo.DiscountRule;

import java.util.List;

/**
 * Defines the contract for a discount strategy.
 * Each implementation will be responsible for applying a specific type of discount.
 */
public interface DiscountStrategy {

    /**
     * Applies a discount rule to a list of checkout items.
     *
     * @param items The list of items in the cart.
     * @param rule  The discount rule to apply.
     * @return a DiscountDto if the discount is applicable, otherwise null.
     */
    CheckoutResponse.DiscountDto applyDiscount(List<CheckoutResponse.CheckoutItemDto> items, DiscountRule rule);

}
