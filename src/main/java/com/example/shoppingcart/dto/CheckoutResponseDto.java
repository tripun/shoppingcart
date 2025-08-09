package com.example.shoppingcart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutResponseDto {
    private int originalTotalPrice;
    private int finalTotalPrice;
    private int totalDiscount;
    private List<AppliedDiscountDto> appliedDiscounts;
    private List<CartItemDto> items;
}
