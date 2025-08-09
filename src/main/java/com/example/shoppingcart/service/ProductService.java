package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.ProductDto;
import com.example.shoppingcart.dto.ProductResponseDto; // Corrected import
import com.example.shoppingcart.model.dynamo.CatalogItem; // Corrected import
import com.example.shoppingcart.service.crud.CrudService;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for product management operations.
 */
public interface ProductService extends CrudService<ProductResponseDto, String, ProductDto> { // Updated CrudService

    /**
     * Creates a new product.
     *
     * @param productDto the DTO containing product information
     * @return the created ProductResponseDto object
     */
    ProductResponseDto createProduct(ProductDto productDto);

    /**
     * Updates an existing product.
     *
     * @param productDto the DTO containing updated product information
     * @return the updated ProductResponseDto object
     */
    ProductResponseDto updateProduct(ProductDto productDto);

    /**
     * Deletes a product by its ID and region.
     *
     * @param productId the ID of the product to delete
     * @param region the region of the product to delete
     */
    void deleteProduct(String productId, String region);

    /**
     * Finds a product by its composite key (ID and region).
     *
     * @param productId The unique ID of the product.
     * @param region    The region for the product.
     * @return an Optional containing the CatalogItem if found.
     */
    Optional<CatalogItem> findByProductIdAndRegion(String productId, String region);

    /**
     * Gets a product by its composite key, throwing an exception if not found.
     *
     * @param productId The unique ID of the product.
     * @param region    The region for the product.
     * @return The found CatalogItem.
     */
    CatalogItem getProductById(String productId, String region);

    /**
     * Gets a product by its name with pagination. Note: This can be inefficient without a GSI.
     *
     * @param name The name of the product.
     * @param pageSize the maximum number of items to retrieve per page
     * @param lastEvaluatedKey the primary key of the last item from the previous page, or null for the first page
     * @return a Page of CatalogItem
     */
    Page<CatalogItem> getProductByNamePaginated(String name, int pageSize, String lastEvaluatedKey);

    /**
     * Gets all products from the catalog with pagination.
     *
     * @param pageSize the maximum number of items to retrieve per page
     * @param lastEvaluatedKey the primary key of the last item from the previous page, or null for the first page
     * @return a Page of CatalogItem
     */
    Page<CatalogItem> getAllProductsPaginated(int pageSize, String lastEvaluatedKey);

    /**
     * Gets all products belonging to a specific category and region.
     */
    List<CatalogItem> getProductsByCategory(String category, String region);

    /**
     * Checks if a product has sufficient stock available.
     */
    boolean isProductAvailable(String productId, String region, int requestedQuantity);

    /**
     * Validates if a product has sufficient stock, throwing an exception if not.
     */
    void validateProductAvailability(String productId, int quantity);

    @Override
    default Optional<ProductResponseDto> getById(String id) {
        // This method is problematic as it doesn't provide a region.
        // It should ideally be removed from CrudService or require a region.
        // For now, delegate to the region-specific method with a default region.
        return findByProductIdAndRegion(id, "UK").map(catalogItem -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(catalogItem.getPk());
            dto.setName(catalogItem.getName());
            dto.setDescription(catalogItem.getDescription());
            dto.setPriceInPence(catalogItem.getPrice());
            dto.setCurrency(catalogItem.getCurrency());
            dto.setCategory(catalogItem.getCategoryHierarchy() != null ? catalogItem.getCategoryHierarchy() : "OTHER");
            dto.setStock(catalogItem.getStock());
            dto.setStatus(catalogItem.getStatus() != null ? catalogItem.getStatus() : ProductResponseDto.ProductStatus.ACTIVE.name());
            dto.setImageUrl(catalogItem.getImageUrl());
            dto.setRegion(catalogItem.getRegion());
            return dto;
        });
    }

    @Override
    default ProductResponseDto update(String id, ProductDto dto) {
        // This method is problematic as it doesn't provide a region.
        // It should ideally be removed from CrudService or require a region.
        // For now, delegate to the region-specific method with a default region.
        dto.setProductId(id);
        dto.setRegion("UK"); // Using a default region
        return updateProduct(dto);
    }

    @Override
    default void delete(String id) {
        // This method is problematic as it doesn't provide a region.
        // It should ideally be removed from CrudService or require a region.
        // For now, delegate to the region-specific method with a default region.
        deleteProduct(id, "UK"); // Using a default region
    }
}
