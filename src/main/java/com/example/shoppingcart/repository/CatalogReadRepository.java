package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import java.util.List;
import java.util.Optional;

public interface CatalogReadRepository {
    Optional<CatalogItem> findById(String productId);
    List<CatalogItem> findByCategory(String categoryHierarchy, int limit);
}
