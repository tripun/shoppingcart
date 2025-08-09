package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.DiscountAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountActionRepository extends JpaRepository<DiscountAction, Long> {
}