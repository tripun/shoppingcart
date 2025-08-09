package com.example.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Represents a product in the shopping cart application.
 * This is a Data Transfer Object (DTO) and is not mapped to a database table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private String productId;
    private String region;
    private String name;
    private String description;
    private Integer priceInPence;
    private String currency;
    private String category;
    private String sku;
    private Integer stock = 0;
    private String status = "ACTIVE";
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Returns the price of the product as a BigDecimal, converted from priceInPence.
     *
     * @return The price of the product in major currency units.
     */
    public BigDecimal getPrice() {
        return priceInPence != null ? new BigDecimal(priceInPence).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * Sets the price of the product, converting from BigDecimal to priceInPence.
     *
     * @param price The price of the product in major currency units.
     */
    public void setPrice(BigDecimal price) {
        this.priceInPence = price != null ? price.multiply(new BigDecimal(100)).intValue() : 0;
    }

    /**
     * Enumeration for product categories.
     */
    public enum ProductCategory {
        FRUITS, VEGETABLES, DAIRY, MEAT, BEVERAGES, SNACKS, FROZEN, BAKERY, HOUSEHOLD, OTHER
    }

    /**
     * Enumeration for product status.
     */
    public enum ProductStatus {
        ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED
    }
}
