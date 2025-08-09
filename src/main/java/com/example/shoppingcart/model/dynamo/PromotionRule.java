package com.example.shoppingcart.model.dynamo;

/**
 * Backwards-compatible alias: PromotionRule now extends DiscountRule so
 * older test code and references continue working while the canonical
 * type used in production is DiscountRule.
 */
public class PromotionRule extends DiscountRule {
    // intentionally empty — inherits everything from DiscountRule
}
