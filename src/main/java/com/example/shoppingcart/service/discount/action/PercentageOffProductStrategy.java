package com.example.shoppingcart.service.discount.action;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ActionStrategy;
import com.example.shoppingcart.service.discount.ActionType;
import org.springframework.stereotype.Component;

@Component
public class PercentageOffProductStrategy implements ActionStrategy {

    @Override
    public ActionType getType() {
        return ActionType.PERCENTAGE_OFF_PRODUCT;
    }

    @Override
    public AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context) {
        String productId = action.getProductId();
        Integer percentage = action.getValue();

        if (productId == null || percentage == null) {
            return new AppliedDiscountDto("Invalid percentage-off-product discount", 0);
        }

        CatalogItem item = context.getCatalogItemMap().get(productId);
        if (item == null || item.getPrice() == null) {
            return new AppliedDiscountDto("Price not found for product " + productId, 0);
        }

        // Calculate the discount amount based on the real price from the catalog
        int discountAmount = (int) (item.getPrice() * (percentage / 100.0));
        String description = String.format("%d%% off %s", percentage, item.getName());

    return new AppliedDiscountDto(description, discountAmount);
    }
}
