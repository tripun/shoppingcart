package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.postgres.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This repository handles WRITE operations to the RDBMS source of truth for Inventory.
@Repository
public interface InventoryWriteRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProductIdAndRegion(String productId, String region);
}
