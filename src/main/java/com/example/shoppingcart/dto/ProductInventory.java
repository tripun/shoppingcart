package com.example.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the inventory level for a specific product in a given region.
 * This is a Data Transfer Object (DTO) and is not mapped to a database table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventory {

    private String productId;
    private String region;
    private Integer quantity;

}
