package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("flatDiscountStrategy")
public class FlatDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(ShoppingCart cart, DiscountRule rule) {
        return rule.getDiscountValue();
    }
}
