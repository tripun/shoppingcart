package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.DiscountRule;

public interface DiscountRuleWriteService {

    /**
     * Creates a new discount rule or updates an existing one in the master RDBMS.
     *
     * @param discountRule The discount rule entity to save.
     * @return The saved discount rule entity.
     */
    DiscountRule createOrUpdateDiscountRule(DiscountRule discountRule);
}
