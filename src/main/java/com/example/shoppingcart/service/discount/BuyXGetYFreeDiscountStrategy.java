package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Discount strategy for "Buy X, Get Y Free" offers.
 * This strategy is identified by the bean name "buyXGetYFreeDiscountStrategy".
 * It calculates the discount by making the cheapest applicable items free.
 */
@Component("buyXGetYFreeDiscountStrategy")
public class BuyXGetYFreeDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(ShoppingCart cart, DiscountRule rule) {
        if (rule.getBuyQuantity() == null || rule.getFreeQuantity() == null || rule.getBuyQuantity() <= rule.getFreeQuantity()) {
            return BigDecimal.ZERO;
        }

        // Flatten all items from the cart that are applicable to this rule
        List<ShoppingCart.CartItemData> applicableItems = cart.getItems().stream()
                .filter(item -> isApplicable(item, rule))
                .collect(Collectors.toList());

        int totalApplicableQuantity = applicableItems.stream().mapToInt(ShoppingCart.CartItemData::getQuantity).sum();

        if (totalApplicableQuantity < rule.getBuyQuantity()) {
            return BigDecimal.ZERO;
        }

        // Calculate how many times the offer can be applied and how many items will be free
        int numberOfOffers = totalApplicableQuantity / rule.getBuyQuantity();
        int numberOfFreeItems = numberOfOffers * rule.getFreeQuantity();

        // Find the cheapest items to apply the discount to
        return applicableItems.stream()
                .sorted(Comparator.comparing(ShoppingCart.CartItemData::getPrice))
                .limit(numberOfFreeItems)
                .map(ShoppingCart.CartItemData::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isApplicable(ShoppingCart.CartItemData item, DiscountRule rule) {
        // In a real scenario, you would check against the product's actual category.
        // This is a simplification assuming the category name is on the cart item.
        return rule.getApplicableCategories().stream()
                .anyMatch(cat -> cat.equalsIgnoreCase(item.getCategory()));
    }
}