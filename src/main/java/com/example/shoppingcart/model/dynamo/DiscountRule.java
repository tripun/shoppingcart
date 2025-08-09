package com.example.shoppingcart.model.dynamo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a discount rule in the shopping cart application.
 * This version includes a minimum spend threshold for discount applicability.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@DynamoDbBean
public class DiscountRule {

    private String ruleId;

    private String ruleName;

    private String description; // Added from PromotionRule

    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal minimumSpend; // New field for subtotal threshold

    private ApplicableTo applicableTo;

    private String applicableValue;

    private String userId; // For user-specific discounts

    private String conditionKey; // For combination discounts

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

    private String activeStatus;

    private Integer priority;

    private Boolean isStackable; // Added from PromotionRule

    private String exclusivityGroup; // Added from PromotionRule

    private Integer buyQuantity;

    private Integer freeQuantity;

    private List<String> applicableCategories;

    private List<String> applicableCurrencies;

    private List<String> applicableRegions;

    private String strategyBeanName;

    private List<Condition> conditions; // Added from PromotionRule

    private List<Action> actions; // Added from PromotionRule

    // Explicit getters for annotated fields
    @DynamoDbPartitionKey
    public String getRuleId() { return ruleId; }

    @DynamoDbAttribute("ruleName")
    public String getRuleName() { return ruleName; }

    @DynamoDbAttribute("description")
    public String getDescription() { return description; }

    @DynamoDbAttribute("startDate")
    public LocalDateTime getStartDate() { return startDate; }

    @DynamoDbAttribute("endDate")
    public LocalDateTime getEndDate() { return endDate; }

    @DynamoDbAttribute("active")
    public boolean isActive() { return active; }

    @DynamoDbAttribute("activeStatus")
    @DynamoDbSecondaryPartitionKey(indexNames = "ActiveRulesByPriorityIndex")
    public String getActiveStatus() { return activeStatus; }

    @DynamoDbAttribute("priority")
    @DynamoDbSecondarySortKey(indexNames = {"ActiveRulesByPriorityIndex", "UserDiscountIndex", "ConditionIndex"})
    public Integer getPriority() { return priority; }

    // Lombok's @Data will provide setters; explicit setters removed to avoid duplication.

    /**
     * Enumeration for discount types.
     */
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        BUY_X_GET_Y_FREE
    }

    /**
     * Enumeration for applicable entities.
     */
    public enum ApplicableTo {
        PRODUCT,
        CATEGORY,
        USER,
        REGION,
        CART // Added for cart-level discounts
    }

    @Data
    @DynamoDbBean
    public static class Condition {
        private String type;
        private String productId;
        private Integer quantity;
        private String operator;
        private Integer value;
        private String tag;
        private String method;
    }

    @Data
    @DynamoDbBean
    public static class Action {
        private String type;
        private String productId;
        private Integer buyQuantity;
        private Integer getQuantity;
        private String categoryPath;
        private Integer value;
    }
}