package com.example.shoppingcart.model.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;

@Data
@RedisHash("Cart")
public class Cart {

    @Id
    private String userId;
    private Map<String, Integer> items = new HashMap<>();
    private String cartId;
    private String currency;
    private String region;
    private String status;
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
    private Map<String, java.math.BigDecimal> appliedDiscounts = new HashMap<>();

    public Cart(String userId) {
        this.userId = userId;
    }

    // No-arg constructor required by some serialization/framework usages
    public Cart() {}

    // Explicit setters (Lombok will also generate them) to avoid compile-time reliance
    public void setCartId(String cartId) { this.cartId = cartId; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setRegion(String region) { this.region = region; }
    public void setStatus(String status) { this.status = status; }
    public void setItems(Map<String,Integer> items) { this.items = items; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setAppliedDiscounts(Map<String, java.math.BigDecimal> appliedDiscounts) { this.appliedDiscounts = appliedDiscounts; }
}
