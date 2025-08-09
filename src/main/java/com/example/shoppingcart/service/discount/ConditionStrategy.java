package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;

public interface ConditionStrategy {
    /**
     * Gets the specific condition type this strategy handles.
     * @return The ConditionType enum.
     */
    ConditionType getType();

    /**
     * Evaluates the condition against the given context.
     * @param condition The specific condition data from the promotion rule.
     * @param context The current evaluation context (cart, user, etc.).
     * @return true if the condition is met, false otherwise.
     */
    boolean evaluate(DiscountRule.Condition condition, EvaluationContext context);
}
