package com.example.shoppingcart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for DiscountRule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Discount Rule")
public class DiscountRuleDto {

    @Schema(description = "Unique identifier for the discount rule.", example = "DR12345")
    private String ruleId;

    @NotBlank(message = "Rule name cannot be blank")
    @Schema(description = "The name of the discount rule.", example = "10% off on all vegetables")
    private String ruleName;

    @Schema(description = "The description of the discount rule.", example = "A detailed description of the discount.")
    private String description;

    @NotNull(message = "Discount type cannot be null")
    @Schema(description = "The type of discount.", example = "PERCENTAGE")
    private DiscountType discountType; // Use internal enum

    @NotNull(message = "Discount value cannot be null")
    @Schema(description = "The value of the discount.", example = "10.00")
    private BigDecimal discountValue;

    @NotNull(message = "ApplicableTo cannot be null")
    @Schema(description = "The entity to which the discount applies.", example = "CATEGORY")
    private ApplicableTo applicableTo; // Use internal enum

    @NotBlank(message = "ApplicableValue cannot be blank")
    @Schema(description = "The value of the applicable entity (e.g., product ID, category name, user ID, region name).", example = "VEGETABLES")
    private String applicableValue;

    @Schema(description = "The start date of the discount.", example = "2025-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "The end date of the discount.", example = "2025-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Whether the discount is currently active.", example = "true")
    private boolean active;

    @Schema(description = "The priority of the discount rule.", example = "1")
    private Integer priority;

    @Schema(description = "Whether the discount is stackable with other discounts.", example = "true")
    private Boolean isStackable;

    @Schema(description = "The exclusivity group for the discount rule.", example = "GROUP_A")
    private String exclusivityGroup;

    @Schema(description = "The number of items to buy for the discount to apply (for BuyXGetYFree).", example = "2")
    private Integer buyQuantity;

    @Schema(description = "The number of items to get for free (for BuyXGetYFree).", example = "1")
    private Integer freeQuantity;

    @Schema(description = "The categories to which the discount applies.")
    private List<String> applicableCategories;

    @Schema(description = "The name of the discount strategy bean.", example = "buyXGetYFreeDiscountStrategy")
    private String strategyBeanName;

    @Schema(description = "List of conditions for the discount rule.")
    private List<ConditionDto> conditions;

    @Schema(description = "List of actions for the discount rule.")
    private List<ActionDto> actions;

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
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Data Transfer Object for Condition")
    public static class ConditionDto {
        private String type;
        private String productId;
        private Integer quantity;
        private String operator;
        private Integer value;
        private String tag;
        private String method;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Data Transfer Object for Action")
    public static class ActionDto {
        private String type;
        private String productId;
        private Integer buyQuantity;
        private Integer getQuantity;
        private String categoryPath;
        private Integer value;
    }
}