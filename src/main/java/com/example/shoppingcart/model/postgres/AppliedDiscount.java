package com.example.shoppingcart.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "applied_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String ruleId;

    private String ruleName;

    private BigDecimal amount;

    private String description;
}
