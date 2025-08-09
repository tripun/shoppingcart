package com.example.shoppingcart.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for direct checkout API that accepts list of product names/IDs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for direct checkout with list of product names")
public class CheckoutRequest {

    @NotNull(message = "Product list cannot be null")
    @NotEmpty(message = "Product list cannot be empty")
    @Schema(description = "List of product names or IDs to checkout",
            example = "[\"Apple\", \"Banana\", \"Melon\"]",
            required = true)
    private List<String> products;

    @Schema(description = "Customer ID for the checkout", example = "customer123")
    private String customerId;

    @Schema(description = "Currency code for pricing", example = "GBP", defaultValue = "GBP")
    private String currency = "GBP";

    @Schema(description = "Region code for regional pricing", example = "UK", defaultValue = "UK")
    private String region = "UK";
}