package com.example.shoppingcart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private List<CartItemDto> items;
    private String userId;
    private String cartId;
    private String currency;
}
