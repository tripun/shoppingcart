package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "discount_actions")
public class DiscountAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "target_product_id")
    private String targetProductId;

    private BigDecimal value;

    @Column(name = "get_quantity")
    private Integer getQuantity;

    public Long getRuleId() { return this.ruleId; }
    public String getActionType() { return this.actionType; }
    public String getTargetProductId() { return this.targetProductId; }
    public Integer getGetQuantity() { return this.getQuantity; }
    public java.math.BigDecimal getValue() { return this.value; }
}