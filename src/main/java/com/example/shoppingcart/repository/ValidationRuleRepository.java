package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.ValidationRule; // Corrected import
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository; // New import
import java.util.List;
import java.util.Optional;

public interface ValidationRuleRepository extends DynamoDBCrudRepository<ValidationRule, String> { // Extended

    List<ValidationRule> findByActiveTrue();
    List<ValidationRule> findByRuleTypeAndActiveTrue(String ruleType);
}
