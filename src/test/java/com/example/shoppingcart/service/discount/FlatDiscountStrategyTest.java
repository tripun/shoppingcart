package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlatDiscountStrategyTest {

    private FlatDiscountStrategy flatDiscountStrategy = new FlatDiscountStrategy();

    @Test
    @DisplayName("Should apply flat discount correctly")
    void testFlatDiscount() {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(Collections.singletonList(createItem("APPLE", 2, new BigDecimal("0.50"))));

        DiscountRule rule = new DiscountRule();
        rule.setRuleName("Flat 5 off");
        rule.setDiscountType(DiscountRule.DiscountType.FIXED_AMOUNT);
        rule.setDiscountValue(new BigDecimal("5.00"));

        // Act
        BigDecimal discount = flatDiscountStrategy.apply(cart, rule);

        // Assert
        assertEquals(new BigDecimal("5.00"), discount);
    }

    @Test
    @DisplayName("Should return zero if discount value is null")
    void testFlatDiscount_NullValue() {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(Collections.singletonList(createItem("APPLE", 2, new BigDecimal("0.50"))));

        DiscountRule rule = new DiscountRule();
        rule.setRuleName("Flat 5 off");
        rule.setDiscountType(DiscountRule.DiscountType.FIXED_AMOUNT);
        rule.setDiscountValue(null);

        // Act
        BigDecimal discount = flatDiscountStrategy.apply(cart, rule);

        // Assert
        assertEquals(BigDecimal.ZERO, discount);
    }

    private ShoppingCart.CartItemData createItem(String productId, int quantity, BigDecimal price) {
        ShoppingCart.CartItemData item = new ShoppingCart.CartItemData();
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setProductName(productId);
        item.setTotalPrice(price.multiply(BigDecimal.valueOf(quantity)));
        return item;
    }
}