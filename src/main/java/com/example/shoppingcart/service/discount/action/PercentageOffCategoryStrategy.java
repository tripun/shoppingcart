package com.example.shoppingcart.service.discount.action;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import com.example.shoppingcart.service.discount.ActionStrategy;
import com.example.shoppingcart.service.discount.ActionType;
import org.springframework.stereotype.Component;

@Component
public class PercentageOffCategoryStrategy implements ActionStrategy {

    @Override
    public ActionType getType() {
        return ActionType.PERCENTAGE_OFF_CATEGORY;
    }

    @Override
    public AppliedDiscountDto apply(DiscountRule.Action action, EvaluationContext context) {
        String categoryPath = action.getCategoryPath();
        Integer percentage = action.getValue();

        if (categoryPath == null || percentage == null) {
            return new AppliedDiscountDto("Invalid percentage-off-category discount", 0);
        }

        // Calculate the subtotal of only the items in the specified category
        int categorySubtotal = 0;
        for (CartItemDto cartItem : context.getCart().getItems()) {
            CatalogItem catalogItem = context.getCatalogItemMap().get(cartItem.getProductId());
            if (catalogItem != null && categoryPath.equals(catalogItem.getCategoryHierarchy())) {
                categorySubtotal += catalogItem.getPrice() * cartItem.getQuantity();
            }
        }

        if (categorySubtotal == 0) {
            return new AppliedDiscountDto("No items in the specified category", 0);
        }

        int discountAmount = (int) (categorySubtotal * (percentage / 100.0));
        String description = String.format("%d%% off all items in category %s", percentage, categoryPath);

        return new AppliedDiscountDto(description, discountAmount);
    }
}
