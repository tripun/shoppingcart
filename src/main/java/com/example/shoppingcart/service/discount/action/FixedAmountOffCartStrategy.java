package com.example.shoppingcart.service.discount.action;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ActionStrategy;
import com.example.shoppingcart.service.discount.ActionType;
import org.springframework.stereotype.Component;

@Component
public class FixedAmountOffCartStrategy implements ActionStrategy {

    @Override
    public ActionType getType() {
        return ActionType.FIXED_AMOUNT_OFF_CART;
    }

    @Override
    public AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context) {
        if (action.getValue() == null) {
            return new AppliedDiscountDto("Invalid fixed amount discount", 0);
        }
        String description = String.format("£%.2f off your order", action.getValue() / 100.0);
        return new AppliedDiscountDto(description, action.getValue());
    }
}
