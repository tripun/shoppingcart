package com.example.shoppingcart.service.discount.action;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ActionStrategy;
import com.example.shoppingcart.service.discount.ActionType;
import org.springframework.stereotype.Component;

@Component
public class ApplyFreeShippingStrategy implements ActionStrategy {

    // In a real system, this value would come from a configuration service.
    private static final int STANDARD_SHIPPING_COST = 500; // 500 pence or £5.00

    @Override
    public ActionType getType() {
        return ActionType.APPLY_FREE_SHIPPING;
    }

    @Override
    public AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context) {
        // The value of this discount is the shipping cost that is being waived.
        return new AppliedDiscountDto("Free Shipping", STANDARD_SHIPPING_COST);
    }
}
