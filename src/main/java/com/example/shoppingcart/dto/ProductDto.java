package com.example.shoppingcart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Product information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Product information")
public class ProductDto {

    @Schema(description = "Unique identifier for the product.", example = "PROD001")
    private String productId;

    @NotBlank(message = "Product name cannot be blank")
    @Schema(description = "The name of the product.", example = "Apple")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    @Schema(description = "A detailed description of the product.", example = "Fresh red apples from New Zealand.")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be non-negative")
    @Schema(description = "The price of the product.", example = "1.20")
    private BigDecimal price;

    @NotBlank(message = "Currency cannot be blank")
    @Schema(description = "The currency in which the product is priced (e.g., GBP, USD).", example = "GBP")
    private String currency;

    @NotNull(message = "Category cannot be null")
    @Schema(description = "The category to which the product belongs.", example = "FRUITS")
    private ProductCategory category; // Use internal enum

    @NotBlank(message = "SKU cannot be blank")
    @Schema(description = "Stock Keeping Unit (SKU) for the product.", example = "APPLE-NZ-RED")
    private String sku;

    @Min(value = 0, message = "Stock must be non-negative")
    @Schema(description = "The current stock level of the product.", example = "100")
    private Integer stock;

    @NotNull(message = "Product status cannot be null")
    @Schema(description = "The current status of the product (e.g., ACTIVE, INACTIVE).", example = "ACTIVE")
    private ProductStatus status; // Use internal enum

    @Schema(description = "URL to the product's image.", example = "https://example.com/apple.jpg")
    private String imageUrl;

    @NotBlank(message = "Region cannot be blank")
    @Schema(description = "The region where the product is available.", example = "UK")
    private String region;

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