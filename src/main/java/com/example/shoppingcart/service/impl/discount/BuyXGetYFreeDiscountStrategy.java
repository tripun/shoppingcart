package com.example.shoppingcart.service.impl.discount;

import com.example.shoppingcart.dto.CheckoutResponse;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.DiscountStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Implements the 'Buy X, Get Y Free' discount logic.
 * This strategy applies when a customer buys a certain quantity of a product and gets another quantity for free.
 */
@Component("buyXGetYFreeDiscountStrategy")
public class BuyXGetYFreeDiscountStrategy implements DiscountStrategy {

    @Override
    public CheckoutResponse.DiscountDto applyDiscount(List<CheckoutResponse.CheckoutItemDto> items, DiscountRule rule) {
        // Find the item in the cart that this rule applies to.
        CheckoutResponse.CheckoutItemDto applicableItem = items.stream()
                .filter(item -> Objects.equals(item.getProductId(), rule.getApplicableValue()))
                .findFirst()
                .orElse(null);

        if (applicableItem == null) {
            return null; // The product for this rule is not in the cart.
        }

        int buyQuantity = rule.getBuyQuantity();
        int freeQuantity = rule.getFreeQuantity();
        int itemQuantity = applicableItem.getQuantity();

        // Check if the quantity is sufficient to trigger the discount.
        if (itemQuantity < buyQuantity) {
            return null;
        }

        // Calculate how many times the offer can be applied.
        int numberOfTimesOfferApplies = itemQuantity / buyQuantity;
        int totalFreeItems = numberOfTimesOfferApplies * freeQuantity;

        if (totalFreeItems == 0) {
            return null;
        }

        // Calculate the total discount amount.
        BigDecimal discountAmount = applicableItem.getUnitPrice().multiply(BigDecimal.valueOf(totalFreeItems));

        String description = String.format("Buy %d Get %d Free on %s", buyQuantity, freeQuantity, applicableItem.getProductName());

        return new CheckoutResponse.DiscountDto(
                rule.getRuleName(),
                rule.getDiscountType().name(),
                rule.getDiscountValue(),
                discountAmount,
                description
        );
    }
}
