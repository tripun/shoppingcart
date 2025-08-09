package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.DiscountRuleDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.service.crud.CrudService;

import java.math.BigDecimal;
import java.util.Optional;

public interface DiscountService extends CrudService<DiscountRule, String, DiscountRuleDto> {
    BigDecimal calculateTotalDiscount(ShoppingCart cart);
    DiscountRule createDiscountRule(DiscountRuleDto discountRuleDto);
    Optional<DiscountRule> getDiscountRuleById(String ruleId);
    void deleteDiscountRule(String ruleId);
}