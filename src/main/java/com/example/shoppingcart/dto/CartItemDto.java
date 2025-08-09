package com.example.shoppingcart.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private String productId;
    private int quantity;
    private int priceInPence;
}
