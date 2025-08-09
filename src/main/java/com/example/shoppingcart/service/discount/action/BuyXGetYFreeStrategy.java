package com.example.shoppingcart.service.discount.action;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ActionStrategy;
import com.example.shoppingcart.service.discount.ActionType;
import org.springframework.stereotype.Component;

@Component
public class BuyXGetYFreeStrategy implements ActionStrategy {

    @Override
    public ActionType getType() {
        return ActionType.BUY_X_GET_Y_FREE;
    }

    @Override
    public AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context) {
        String productId = action.getProductId();
        Integer getQuantity = action.getGetQuantity();

        if (productId == null || getQuantity == null) {
            return new AppliedDiscountDto("Invalid BOGO discount", 0);
        }

        CatalogItem item = context.getCatalogItemMap().get(productId);
        if (item == null || item.getPrice() == null) {
            return new AppliedDiscountDto("Price not found for product " + productId, 0);
        }

        // The value of the discount is the price of the items the customer gets for free.
    int discountAmount = item.getPrice() * getQuantity;
    String description = String.format("Buy %d Get %d Free on %s", action.getBuyQuantity(), getQuantity, item.getName());

        return new AppliedDiscountDto(description, discountAmount);
    }
}
