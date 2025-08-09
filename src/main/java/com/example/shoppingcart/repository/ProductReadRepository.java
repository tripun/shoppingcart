package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.CatalogItem; // Corrected import
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entities, focused on read operations from DynamoDB.
 */
public interface ProductReadRepository { // Renamed interface

    /**
     * Finds a product by its product ID and a specific region.
     *
     * @param productId the ID of the product
     * @param region    the region of the product
     * @return an Optional containing the fully assembled CatalogItem, or empty if not found
     */
    Optional<CatalogItem> findByProductIdAndRegion(String productId, String region);

    /**
     * Finds all products for a given collection of product IDs in a specific region.
     *
     * @param productIds The collection of product IDs to find.
     * @param region     The region for the products.
     * @return A list of found CatalogItem.
     */
    List<CatalogItem> findAllByProductIdIn(Collection<String> productIds, String region);

    /**
     * Finds products by category and region.
     *
    * @param categoryHierarchy the category hierarchy string (e.g. "FOOD/FRUITS")
    * @param limit             maximum number of items to return
    * @return a list of CatalogItem in the specified category hierarchy
     */
    List<CatalogItem> findByCategory(String categoryHierarchy, int limit);

    /**
     * Finds a product by its product ID. Note: This method has a known bug in the implementation
     * where the region is hardcoded.
     *
     * @param productId the ID of the product
     * @return an Optional containing the CatalogItem, or empty if not found
     */
    Optional<CatalogItem> findByProductId(String productId);

    /**
     * Retrieves all products with pagination. (Currently Not Supported)
     */
    PageIterable<CatalogItem> findAllPaginated(int pageSize, String lastEvaluatedKey);

    /**
     * Finds products by name with pagination. (Currently Not Supported)
     */
    PageIterable<CatalogItem> findByNamePaginated(String name, int pageSize, String lastEvaluatedKey);
}