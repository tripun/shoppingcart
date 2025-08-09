package com.example.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the price of a product for a specific region and currency.
 * This is a Data Transfer Object (DTO) and is not mapped to a database table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    private String productId;
    private String region;
    private String currency;
    private Integer priceInSmallestUnit;

}
