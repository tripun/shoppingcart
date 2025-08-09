package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "discount_conditions")
public class DiscountCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    @Column(name = "product_id")
    private String productId;

    private Integer quantity;

    @Column(name = "amount_in_smallest_unit")
    private Integer amountInSmallestUnit;

    public Long getRuleId() { return this.ruleId; }
    public String getConditionType() { return this.conditionType; }
    public String getProductId() { return this.productId; }
    public Integer getQuantity() { return this.quantity; }
    public Integer getAmountInSmallestUnit() { return this.amountInSmallestUnit; }
}