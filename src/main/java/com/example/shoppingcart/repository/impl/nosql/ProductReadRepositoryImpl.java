package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.CatalogItem; // Corrected import
import com.example.shoppingcart.model.dynamo.ProductCatalogItem; // Corrected import
import com.example.shoppingcart.repository.ProductCatalogRepository;
import com.example.shoppingcart.repository.ProductReadRepository; // Corrected import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NoSQL implementation of the ProductReadRepository that acts as a facade over the single-table ProductCatalogRepository.
 * It translates between the business-facing CatalogItem model and the persistence-focused ProductCatalogItem model.
 */
@Repository("productReadRepository") // Renamed bean name
@RequiredArgsConstructor
public class ProductReadRepositoryImpl implements ProductReadRepository { // Renamed class and implemented interface

    private final ProductCatalogRepository productCatalogRepository;

    private static final String METADATA_SK = "METADATA";
    private static final String PRICE_SK_PREFIX = "PRICE#";
    private static final String INVENTORY_SK_PREFIX = "INVENTORY#";

    @Override
    public Optional<CatalogItem> findByProductIdAndRegion(String productId, String region) {
        List<ProductCatalogItem> items = productCatalogRepository.findByProductId(productId);
        return toCatalogItem(items, region);
    }

    @Override
    public List<CatalogItem> findAllByProductIdIn(Collection<String> productIds, String region) {
        return productIds.stream()
                .map(productId -> findByProductIdAndRegion(productId, region))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatalogItem> findByCategory(String categoryHierarchy, int limit) {
        List<ProductCatalogItem> metadataItems = productCatalogRepository.findByCategory(categoryHierarchy, limit);
        Set<String> productIds = metadataItems.stream()
                .map(ProductCatalogItem::getProductId)
                .collect(Collectors.toSet());

    // default region to UK for cross-region queries unless caller supplies different method
    String region = "UK";
    return findAllByProductIdIn(productIds, region);
    }

    @Override
    public Optional<CatalogItem> findByProductId(String productId) {
        return findByProductIdAndRegion(productId, "UK"); // Default region
    }

    private Optional<CatalogItem> toCatalogItem(List<ProductCatalogItem> items, String region) {
        if (items == null || items.isEmpty()) {
            return Optional.empty();
        }

        CatalogItem catalogItem = new CatalogItem();
        catalogItem.setRegion(region);

        items.stream()
                .filter(item -> METADATA_SK.equals(item.getSk()))
                .findFirst()
                .ifPresent(item -> {
                    catalogItem.setPk(item.getProductId());
                    catalogItem.setSk(item.getSk());
                    catalogItem.setName(item.getName());
                    catalogItem.setDescription(item.getDescription());
                    catalogItem.setCategoryHierarchy(item.getCategory()); // Assuming category is hierarchy
                    catalogItem.setStatus(item.getStatus());
                    catalogItem.setImageUrl(item.getImageUrl());
                });

        if (catalogItem.getPk() == null) { // Check if metadata was found
            return Optional.empty();
        }

        items.stream()
                .filter(item -> item.getSk().startsWith(PRICE_SK_PREFIX + region)) // Filter by region for price
                .findFirst()
                .ifPresent(item -> {
                    catalogItem.setPrice(item.getPriceInSmallestUnit());
                    catalogItem.setCurrency(item.getCurrency());
                });

        items.stream()
                .filter(item -> item.getSk().startsWith(INVENTORY_SK_PREFIX + region)) // Filter by region for inventory
                .findFirst()
                .ifPresent(item -> catalogItem.setStock(item.getStock()));

        return Optional.of(catalogItem);
    }

    @Override
    public PageIterable<CatalogItem> findAllPaginated(int pageSize, String lastEvaluatedKey) {
        throw new UnsupportedOperationException("findAllPaginated is not supported. Use key-based lookups for performance.");
    }

    @Override
    public PageIterable<CatalogItem> findByNamePaginated(String name, int pageSize, String lastEvaluatedKey) {
        throw new UnsupportedOperationException("findByNamePaginated is not supported. Use a dedicated search index for this functionality.");
    }
}