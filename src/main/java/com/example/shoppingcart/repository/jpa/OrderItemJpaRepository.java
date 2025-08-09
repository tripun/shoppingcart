package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}