package com.example.shoppingcart.service.discount.strategy;

import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.model.dynamo.PromotionRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ConditionStrategy;
import com.example.shoppingcart.service.discount.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class CartContainsStrategy implements ConditionStrategy {

    @Override
    public ConditionType getType() {
        return ConditionType.CART_CONTAINS;
    }

    @Override
    public boolean evaluate(PromotionRule.Condition condition, EvaluationContext context) {
        if (condition.getProductId() == null || condition.getQuantity() == null) {
            return false;
        }
        return context.getCart().getItems().stream()
                .filter(item -> item.getProductId().equals(condition.getProductId()))
                .mapToInt(CartItemDto::getQuantity)
                .sum() >= condition.getQuantity();
    }
}
