package com.example.shoppingcart.service.discount.strategy;

import com.example.shoppingcart.model.dynamo.PromotionRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ConditionStrategy;
import com.example.shoppingcart.service.discount.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class CartSubtotalStrategy implements ConditionStrategy {

    @Override
    public ConditionType getType() {
        return ConditionType.CART_SUBTOTAL;
    }

    @Override
    public boolean evaluate(PromotionRule.Condition condition, EvaluationContext context) {
        if (condition.getValue() == null || condition.getOperator() == null || context.getCartSubtotal() == null) {
            return false;
        }
        // This is a simplified implementation. A real one would handle all operators.
        if ("GREATER_THAN_OR_EQUAL".equalsIgnoreCase(condition.getOperator())) {
            return context.getCartSubtotal() >= condition.getValue();
        }
        return false;
    }
}
