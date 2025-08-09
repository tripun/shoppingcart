package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.ProductCatalogItem; // Corrected import
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The central repository for the ProductCatalog table, based on the single-table design.
 * This is the only repository that directly interacts with the physical DynamoDB table.
 */
public interface ProductCatalogRepository extends CrudRepository<ProductCatalogItem, String> {

    /**
     * Finds a specific item in the product catalog using its composite key.
     *
     * @param productId The partition key (e.g., "APPLE").
     * @param sk The sort key (e.g., "METADATA", "PRICE#UK#GBP").
     * @return The matching ProductCatalogItem, or null if not found.
     */
    ProductCatalogItem findByProductIdAndSk(String productId, String sk);

    /**
     * Finds all items related to a specific product ID (metadata, all prices, all inventory).
     *
     * @param productId The partition key.
     * @return A list of all related ProductCatalogItems.
     */
    List<ProductCatalogItem> findByProductId(String productId);

    /**
     * Finds all items for a specific product that have a sort key starting with a given prefix.
     * This is useful for finding all prices or all inventory for a product.
     *
     * @param productId The partition key.
     * @param skPrefix The prefix for the sort key (e.g., "PRICE#", "INVENTORY#").
     * @return A list of matching ProductCatalogItems.
     */
    List<ProductCatalogItem> findByProductIdAndSkStartsWith(String productId, String skPrefix);

    /**
     * Finds all product metadata items within a specific category and region using the CategoryIndex GSI.
     *
    * @param categoryHierarchy The category hierarchy to search for (e.g. "FOOD/FRUITS").
    * @param limit             Maximum number of items to return.
    * @return A list of product metadata items in that category hierarchy.
    */
    List<ProductCatalogItem> findByCategory(String categoryHierarchy, int limit);
}