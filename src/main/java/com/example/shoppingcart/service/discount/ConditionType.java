package com.example.shoppingcart.service.discount;

import java.util.Arrays;

public enum ConditionType {
    USER_HAS_TAG,
    CART_SUBTOTAL,
    CART_CONTAINS,
    PAYMENT_METHOD_IS;

    public static ConditionType fromString(String text) {
        return Arrays.stream(ConditionType.values())
                .filter(e -> e.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
