package com.example.shoppingcart.service;

import com.example.shoppingcart.model.redis.Cart;

public interface CartWriteService {

    Cart createCart(String userId);

    Cart addItem(String cartId, String productId, int quantity, int priceInSmallestUnit);

    Cart removeItem(String cartId, String productId);

    Cart updateItemQuantity(String cartId, String productId, int quantity);

    Cart checkout(String cartId);
}
