package com.example.shoppingcart.model.dynamo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ShoppingCart implements Serializable {

    private String cartId;

    private String userId;

    private CartStatus status;
    private List<CartItemData> items = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal total;
    private String currency;
    private String region;
    private Instant createdAt;
    private Instant updatedAt;
    private Map<String, BigDecimal> appliedDiscounts;

    private String sessionId;

    @DynamoDbPartitionKey
    public String getCartId() { return cartId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "UserCartIndex")
    public String getUserId() { return userId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "SessionIndex")
    public String getSessionId() { return sessionId; }

    private String notes;

    /**
     * Cart status enum
     */
    public enum CartStatus {
        ACTIVE, CHECKED_OUT, SAVED, DELETED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DynamoDbBean
    public static class CartItemData implements Serializable {
        private String productId;
        private int quantity;
        private BigDecimal price; // Price per item in pence
        private BigDecimal totalPrice;
        private String productName;
        private String description;
        private String imageUrl;
        private String category;
    }
}
