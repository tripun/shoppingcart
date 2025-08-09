package com.example.shoppingcart.service.discount.strategy;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ConditionStrategy;
import com.example.shoppingcart.service.discount.ConditionType;
import org.springframework.stereotype.Component;

@Component
public class UserHasTagStrategy implements ConditionStrategy {

    @Override
    public ConditionType getType() {
        return ConditionType.USER_HAS_TAG;
    }

    @Override
    public boolean evaluate(DiscountRule.Condition condition, EvaluationContext context) {
        if (condition.getTag() == null || context.getUserTags() == null) {
            return false;
        }
        return context.getUserTags().contains(condition.getTag());
    }
}
