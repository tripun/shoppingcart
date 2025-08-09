package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.ProductPrice;

public interface ProductPriceWriteService {
    ProductPrice createOrUpdateProductPrice(ProductPrice productPrice);
    void deleteProductPrice(Long id);
}