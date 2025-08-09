package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryJpaRepository extends JpaRepository<Inventory, Long> {
}