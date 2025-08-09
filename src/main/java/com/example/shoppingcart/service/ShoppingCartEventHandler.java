package com.example.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShoppingCartEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartEventHandler.class);

    private final CacheManager cacheManager;

    
    public void handleShoppingCartEvent(Map<String, Object> event) {
        String eventType = (String) event.get("type");
        String cartId = (String) event.get("cartId");

        log.info("Received shopping cart event: {} for cart: {}", eventType, cartId);

        switch (eventType) {
            case "item-added":
                Cache cartTotalsCache = cacheManager.getCache("cart-totals");
                if (cartTotalsCache != null) {
                    cartTotalsCache.evict(cartId);
                }
                break;
            case "checkout-completed":
                Cache cartTotalsCacheOnCheckout = cacheManager.getCache("cart-totals");
                if (cartTotalsCacheOnCheckout != null) {
                    cartTotalsCacheOnCheckout.evict(cartId);
                }
                Cache itemPricesCache = cacheManager.getCache("item-prices");
                if (itemPricesCache != null) {
                    itemPricesCache.clear();
                }
                break;
            default:
                log.warn("Unknown event type: {}", eventType);
        }
    }
}