package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;

import java.util.Set;

public interface CheckoutService {

    /**
     * Orchestrates the entire checkout process.
     * 1. Validates stock and product information.
     * 2. Fetches all applicable promotions.
     * 3. Evaluates promotion conditions.
     * 4. Resolves conflicts and determines the best possible discount outcome for the customer.
     * 5. Applies the winning discounts and calculates the final price.
     * 6. Sanitizes the final price to ensure it is not negative.
     *
     * @param cart The user's shopping cart DTO.
     * @param userTags A set of tags associated with the user (e.g., "PRIME_MEMBER").
     * @param paymentMethod The payment method selected by the user.
     * @param region The region for stock and price validation.
     * @return A DTO containing the full breakdown of the checkout calculation.
     * @throws IllegalStateException if an item is out of stock.
     * @throws IllegalArgumentException if an item ID is invalid or the cart is empty.
     */
    CheckoutResponseDto calculateFinalPrice(CartDto cart, Set<String> userTags, String paymentMethod, String region);
}
