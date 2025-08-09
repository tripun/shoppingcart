package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.exception.ErrorCode;
import com.example.shoppingcart.exception.ShoppingCartException;
import com.example.shoppingcart.model.redis.Cart;
import com.example.shoppingcart.repository.CartRepository;
import com.example.shoppingcart.service.CartReadService;
import com.example.shoppingcart.service.InventoryReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartReadServiceImpl implements CartReadService {

    private static final Logger log = LoggerFactory.getLogger(CartReadServiceImpl.class);

    private final CartRepository cartRepository;
    private final InventoryReadService inventoryReadService;

    @Autowired
    public CartReadServiceImpl(CartRepository cartRepository, InventoryReadService inventoryReadService) {
        this.cartRepository = cartRepository;
        this.inventoryReadService = inventoryReadService;
    }

    @Override
    public Cart getCart(String userId) {
        log.debug("Fetching cart for user: {}", userId);
        return cartRepository.findById(userId).orElseGet(() -> {
            Cart c = new Cart(userId);
            return cartRepository.save(c);
        });
    }
}
