package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class EvaluationContext {
    private final CartDto cart;
    private final Set<String> userTags;
    private final String paymentMethod;
    private final Map<String, CatalogItem> catalogItemMap; // Holds details for all products in the cart

    @Setter
    private Integer cartSubtotal;
}
