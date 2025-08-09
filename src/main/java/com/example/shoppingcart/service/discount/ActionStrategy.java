package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;

public interface ActionStrategy {
    /**
     * Gets the specific action type this strategy handles.
     * @return The ActionType enum.
     */
    ActionType getType();

    /**
     * Applies the discount action and calculates its monetary value.
     * @param action The specific action data from the promotion rule.
     * @param context The current evaluation context (cart, user, etc.).
     * @return An AppliedDiscountDto containing the description and calculated value of the discount.
     */
    AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context);
}
