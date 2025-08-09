package com.example.shoppingcart.service.discount;

import java.util.Arrays;

public enum ActionType {
    PERCENTAGE_OFF_PRODUCT,
    BUY_X_GET_Y_FREE,
    PERCENTAGE_OFF_CATEGORY,
    FIXED_AMOUNT_OFF_CART,
    APPLY_FREE_SHIPPING;

    public static ActionType fromString(String text) {
        return Arrays.stream(ActionType.values())
                .filter(e -> e.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
