package com.example.shoppingcart.service.discount.strategy;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ConditionStrategy;
import com.example.shoppingcart.service.discount.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodIsStrategy implements ConditionStrategy {

    @Override
    public ConditionType getType() {
        return ConditionType.PAYMENT_METHOD_IS;
    }

    @Override
    public boolean evaluate(DiscountRule.Condition condition, EvaluationContext context) {
        if (condition.getMethod() == null || context.getPaymentMethod() == null) {
            return false;
        }
        return context.getPaymentMethod().equalsIgnoreCase(condition.getMethod());
    }
}
