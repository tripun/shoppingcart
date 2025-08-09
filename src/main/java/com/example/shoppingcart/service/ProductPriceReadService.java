package com.example.shoppingcart.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.example.shoppingcart.model.ProductPrice; // Corrected import

/**
 * Service interface for product price read operations.
 */
public interface ProductPriceReadService {

    /**
     * Retrieves a product price by its ID and region.
     *
     * @param productId The ID of the product.
     * @param region The region of the product.
     * @return An Optional containing the ProductPrice if found, otherwise empty.
     */
    Optional<ProductPrice> getProductPriceById(String productId, String region);

    /**
     * Calculates the effective price of a product, considering any special rules or discounts.
     *
     * @param productId The ID of the product.
     * @param region The region of the product.
     * @return The calculated effective price.
     */
    BigDecimal calculateEffectivePrice(String productId, String region);
}