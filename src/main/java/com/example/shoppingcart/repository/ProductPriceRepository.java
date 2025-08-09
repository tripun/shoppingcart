package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.ProductPrice;

import java.util.Optional;

/**
 * Facade repository for product pricing domain operations. This interface intentionally does
 * not extend the low-level DynamoDB CRUD contract because pricing is stored in a single-table
 * that uses a different persistence model (ProductCatalogItem). Implementations act as
 * translators between the domain model and the persistence model.
 */
public interface ProductPriceRepository {
    <S extends ProductPrice> S save(S price);

    Optional<ProductPrice> findPrice(String productId, String region, String currency);

    void deleteById(String productId, String region, String currency);

    void delete(ProductPrice price);
}
