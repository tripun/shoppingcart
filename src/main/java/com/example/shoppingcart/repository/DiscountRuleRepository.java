package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.DiscountRule; // Corrected import
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository;

import java.util.List;

/**
 * Repository interface for DiscountRule entities.
 * This version is corrected to provide a single, efficient method for finding active rules
 * that is possible to implement correctly without hardcoded values or inefficient scans.
 */
public interface DiscountRuleRepository extends DynamoDBCrudRepository<DiscountRule, String> {

    /**
     * Saves a list of discount rules in a single batch operation.
     * This is useful for data initialization.
     *
     * @param discountRules the list of discount rules to save.
     */
    void saveAll(List<DiscountRule> discountRules);

    /**
     * Finds all currently active discount rules, ordered by priority.
     *
     * @return A list of active and applicable discount rules, sorted by priority.
     */
    List<DiscountRule> findActiveAndOrderedRules();

    List<DiscountRule> findActiveRulesByProductAndHierarchy(String productId, List<String> categoryHierarchy);

}