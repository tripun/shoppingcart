package com.example.shoppingcart.service;

import com.example.shoppingcart.model.redis.Cart;

public interface CartReadService {

    /**
     * Retrieves a user's cart from the cache, or creates a new one if it doesn't exist.
     *
     * @param userId The ID of the user.
     * @return The user's Cart object.
     */
    Cart getCart(String userId);
}
