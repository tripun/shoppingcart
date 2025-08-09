package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.DiscountCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountConditionRepository extends JpaRepository<DiscountCondition, Long> {
}