package com.example.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedDiscountDto {
    private String ruleId;
    private String ruleName;
    private String description;
    private int amount;

    // Convenience constructor used by action strategies
    public AppliedDiscountDto(String description, int amount) {
        this.description = description;
        this.amount = amount;
    }
}
