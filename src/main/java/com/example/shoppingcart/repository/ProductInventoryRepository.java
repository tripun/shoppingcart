package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.ProductInventory;

import java.util.Optional;

/**
 * Facade repository for product inventory domain operations. Does not extend the low-level
 * DynamoDB CRUD contract because the persistence model differs from the domain model.
 */
public interface ProductInventoryRepository {
    <S extends ProductInventory> S save(S inventory);

    Optional<ProductInventory> findById(String productId, String region);

    void deleteById(String productId, String region);

    void delete(ProductInventory inventory);
}
