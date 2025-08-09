package com.example.shoppingcart.service;

public interface InventoryWriteService {

    /**
     * Updates the master inventory record in the primary RDBMS.
     * This change will then be propagated to the read replica (DynamoDB) via CDC.
     *
     * @param productId The ID of the product to update.
     * @param region The region of the inventory to update.
     * @param quantityChange The change in quantity (can be positive or negative).
     */
    void updateInventory(String productId, String region, int quantityChange);
}
