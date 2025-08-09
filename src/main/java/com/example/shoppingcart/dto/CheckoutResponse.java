package com.example.shoppingcart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for checkout response containing total price and breakdown.
 * Implements the pricing and discount engine results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for checkout operation with pricing breakdown")
public class CheckoutResponse {

    @Schema(description = "Generated cart ID for this checkout", example = "DIRECT-12345678")
    private String cartId;

    @Schema(description = "List of items in the checkout")
    private List<CheckoutItemDto> items;

    @Schema(description = "Subtotal before discounts", example = "2.50")
    private BigDecimal subtotal;

    @Schema(description = "Total discount amount", example = "0.25")
    private BigDecimal totalDiscount;

    @Schema(description = "Final total price after discounts", example = "2.25")
    private BigDecimal totalPrice;

    @Schema(description = "Currency code", example = "GBP")
    private String currency;

    @Schema(description = "Applied discount details")
    private List<DiscountDto> appliedDiscounts;

    @Schema(description = "Checkout timestamp")
    private LocalDateTime checkoutTime;

    /**
     * Individual item in checkout response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual item in checkout")
    public static class CheckoutItemDto {
        @Schema(description = "Product name", example = "Apple")
        private String productName;

        @Schema(description = "Product ID", example = "APPLE")
        private String productId;

        @Schema(description = "Quantity", example = "2")
        private Integer quantity;

        @Schema(description = "Unit price in pounds", example = "0.35")
        private BigDecimal unitPrice;

        @Schema(description = "Total price for this item", example = "0.70")
        private BigDecimal totalPrice;

        @Schema(description = "Category", example = "FRUITS")
        private String category;
    }

    /**
     * Applied discount information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Applied discount information")
    public static class DiscountDto {
        @Schema(description = "Discount name", example = "MELON_BOGO")
        private String name;

        @Schema(description = "Discount type", example = "BUY_X_GET_Y_FREE")
        private String type;

        @Schema(description = "Discount value", example = "50.00")
        private BigDecimal value;

        @Schema(description = "Discount amount applied", example = "0.25")
        private BigDecimal discountAmount;

        @Schema(description = "Description", example = "Buy one get one free on melons")
        private String description;
    }
}
