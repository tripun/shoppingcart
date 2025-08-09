package com.example.shoppingcart.model.dynamo;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
@Data
@NoArgsConstructor
@DynamoDbBean
public class ValidationRule {
    private String id;
    private String ruleType;
    private String ruleValue;
    private String description;
    private boolean active = true;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("ruleType")
    public String getRuleType() {
        return ruleType;
    }

    @DynamoDbAttribute("ruleValue")
    public String getRuleValue() {
        return ruleValue;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    @DynamoDbAttribute("active")
    public boolean isActive() {
        return active;
    }

    public enum RuleType {
        MAX_BASKET_ITEMS,
        MIN_PRICE,
        MAX_PRICE,
        PRODUCT_ID_LENGTH,
        MIN_QUANTITY,
        MAX_QUANTITY
    }
}