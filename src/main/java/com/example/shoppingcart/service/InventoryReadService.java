package com.example.shoppingcart.service;

public interface InventoryReadService {

    /**
     * Checks the read replica (DynamoDB) to see if a product is in stock.
     *
     * @param productId The ID of the product to check.
     * @param region The region of the inventory to check.
     * @param requestedQuantity The quantity requested by the user.
     * @return true if the item is available and has sufficient stock, false otherwise.
     */
    boolean isInStock(String productId, String region, int requestedQuantity);
}
