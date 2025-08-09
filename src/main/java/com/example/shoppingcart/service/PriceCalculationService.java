package com.example.shoppingcart.service;

import com.example.shoppingcart.model.dynamo.ShoppingCart;

/**
 * Service responsible for calculating prices and totals for a shopping cart.
 */
public interface PriceCalculationService {
    /**
     * Calculates the subtotal, total discount, and final total for a given shopping cart.
     * This method mutates the passed-in ShoppingCart object, setting the calculated values.
     *
     * @param cart The ShoppingCart object to calculate totals for.
     */
    void calculateCartTotals(ShoppingCart cart);
}
