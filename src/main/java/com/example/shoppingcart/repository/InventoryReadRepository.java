package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.InventoryRecord;
import java.util.Optional;

public interface InventoryReadRepository {
    Optional<InventoryRecord> findById(String productId, String region);
}