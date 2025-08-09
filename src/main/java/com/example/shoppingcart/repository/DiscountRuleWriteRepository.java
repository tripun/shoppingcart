package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.postgres.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This repository handles WRITE operations to the RDBMS source of truth for Discount Rules.
@Repository
public interface DiscountRuleWriteRepository extends JpaRepository<DiscountRule, Long> {
}
