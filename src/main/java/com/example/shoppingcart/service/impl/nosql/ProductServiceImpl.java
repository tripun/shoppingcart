package com.example.shoppingcart.service.impl.nosql;

import com.example.shoppingcart.config.AppConstants;
import com.example.shoppingcart.dto.ProductDto;
import com.example.shoppingcart.dto.ProductResponseDto;
import com.example.shoppingcart.exception.ErrorCode;
import com.example.shoppingcart.exception.ShoppingCartException;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.postgres.Product; // Corrected import
import com.example.shoppingcart.repository.CatalogReadRepository;
import com.example.shoppingcart.repository.ProductWriteRepository;
import com.example.shoppingcart.service.InventoryReadService; // New import
import com.example.shoppingcart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("productService")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductWriteRepository productWriteRepository;
    private final CatalogReadRepository catalogReadRepository;
    private final InventoryReadService inventoryReadService;

    @Override
    public ProductResponseDto createProduct(ProductDto productDto) {
        Product product = new Product();
        // set id only if provided
        if (productDto.getProductId() != null) {
            product.setId(productDto.getProductId());
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
    product.setCategoryId(productDto.getCategory() != null ? (long) productDto.getCategory().ordinal() + 1L : null); // map enum to categoryId
        product.setSku(productDto.getSku());
    product.setStatus(productDto.getStatus() != null ? productDto.getStatus().name() : null);
        product.setImageUrl(productDto.getImageUrl());
        product.setCreatedAtUtc(LocalDateTime.now());
        product.setUpdatedAtUtc(LocalDateTime.now());
        productWriteRepository.save(product);

        // For now, we are not creating a CatalogItem directly here.
        // It is assumed that CDC will handle the propagation to DynamoDB.
        // We return a ProductResponseDto based on the input DTO.
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setProductId(productDto.getProductId());
        responseDto.setName(productDto.getName());
        responseDto.setDescription(productDto.getDescription());
        responseDto.setPriceInPence(productDto.getPrice().intValue()); // Assuming price is in pence
        responseDto.setCurrency(productDto.getCurrency());
    responseDto.setCategory(productDto.getCategory() != null ? productDto.getCategory().name() : null);
    responseDto.setSku(productDto.getSku());
    responseDto.setStock(productDto.getStock());
    responseDto.setStatus(productDto.getStatus() != null ? productDto.getStatus().name() : null);
        responseDto.setImageUrl(productDto.getImageUrl());
        responseDto.setRegion(productDto.getRegion());
        return responseDto;
    }

    @Override
    @CacheEvict(value = AppConstants.CacheConstants.PRODUCTS_CACHE, key = "#productDto.productId + '_' + #productDto.region")
    public ProductResponseDto updateProduct(ProductDto productDto) {
        // Fetch the existing entity by String id
        String pid = productDto.getProductId();
        if (pid == null) {
            throw new ShoppingCartException(ErrorCode.PROD_101_INVALID_ID_FORMAT, "Product id is required for update");
        }
        Product existingProduct = productWriteRepository.findById(pid)
            .orElseThrow(() -> new ShoppingCartException(ErrorCode.PROD_100_NOT_FOUND, "Product not found: " + productDto.getProductId()));

        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setCategoryId(productDto.getCategory().ordinal() + 1L);
        existingProduct.setSku(productDto.getSku());
        existingProduct.setStatus(productDto.getStatus().name());
        existingProduct.setImageUrl(productDto.getImageUrl());
        existingProduct.setUpdatedAtUtc(LocalDateTime.now());
        productWriteRepository.save(existingProduct);

        // Return a ProductResponseDto based on the updated DTO.
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setProductId(productDto.getProductId());
        responseDto.setName(productDto.getName());
        responseDto.setDescription(productDto.getDescription());
        responseDto.setPriceInPence(productDto.getPrice().intValue()); // Assuming price is in pence
        responseDto.setCurrency(productDto.getCurrency());
    responseDto.setCategory(productDto.getCategory() != null ? productDto.getCategory().name() : null);
        responseDto.setSku(productDto.getSku());
        responseDto.setStock(productDto.getStock());
    responseDto.setStatus(productDto.getStatus() != null ? productDto.getStatus().name() : null);
        responseDto.setImageUrl(productDto.getImageUrl());
        responseDto.setRegion(productDto.getRegion());
        return responseDto;
    }

    @Override
    @CacheEvict(value = AppConstants.CacheConstants.PRODUCTS_CACHE, key = "#productId + '_' + #region")
    public void deleteProduct(String productId, String region) {
        // Delete from PostgreSQL
    // delete by String id
    productWriteRepository.deleteById(productId);
        // Assuming CDC handles deletion from DynamoDB
    }

    @Override
    @Cacheable(value = AppConstants.CacheConstants.PRODUCTS_CACHE, key = "#productId + '_' + #region")
    public Optional<CatalogItem> findByProductIdAndRegion(String productId, String region) {
        log.debug("Searching for product by ID: {} in region: {}", productId, region);
    Optional<CatalogItem> product = catalogReadRepository.findById(productId);
        log.debug("Product found: {}", product.isPresent());
        return product;
    }

    @Override
    @Cacheable(value = AppConstants.CacheConstants.PRODUCTS_CACHE, key = "#productId + '_' + #region")
    public CatalogItem getProductById(String productId, String region) {
    return catalogReadRepository.findById(productId)
            .orElseThrow(() -> new ShoppingCartException(ErrorCode.PROD_100_NOT_FOUND,
                "Product not found with ID: " + productId + " in region: " + region));
    }

    @Override
    public Page<CatalogItem> getProductByNamePaginated(String name, int pageSize, String lastEvaluatedKey) {
        throw new UnsupportedOperationException("Finding products by name is not supported. Use a dedicated search index for this functionality.");
    }

    @Override
    public Page<CatalogItem> getAllProductsPaginated(int pageSize, String lastEvaluatedKey) {
        throw new UnsupportedOperationException("Finding all products is not supported. Use key-based lookups for performance.");
    }

    @Override
    @Cacheable(value = AppConstants.CacheConstants.PRODUCTS_CACHE, key = "#category + '_' + #region")
    public List<CatalogItem> getProductsByCategory(String category, String region) {
    // Use the new repository method which accepts a category hierarchy and a limit.
    int limit = AppConstants.Api.DEFAULT_PAGE_SIZE;
    return catalogReadRepository.findByCategory(category, limit);
    }

    @Override
    public boolean isProductAvailable(String productId, String region, int requestedQuantity) {
        return inventoryReadService.isInStock(productId, region, requestedQuantity);
    }

    @Override
    public void validateProductAvailability(String productId, int quantity) {
        if (!isProductAvailable(productId, AppConstants.Pricing.DEFAULT_REGION, quantity)) {
            throw new ShoppingCartException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK,
                String.format("Insufficient stock for %s. Requested: %d", productId, quantity));
        }
    }

    @Override
    public ProductResponseDto create(ProductDto dto) {
        return createProduct(dto);
    }

    @Override
    public Optional<ProductResponseDto> getById(String id) {
        return findByProductIdAndRegion(id, AppConstants.Pricing.DEFAULT_REGION).map(catalogItem -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(catalogItem.getPk());
            dto.setName(catalogItem.getName());
            dto.setDescription(catalogItem.getDescription());
            dto.setPriceInPence(catalogItem.getPrice());
            dto.setCurrency(catalogItem.getCurrency());
            // Derive a simple category string from the stored categoryHierarchy (take last segment)
            String categoryHierarchy = catalogItem.getCategoryHierarchy();
            String categoryStr = "OTHER";
            if (categoryHierarchy != null && !categoryHierarchy.isEmpty()) {
                String[] parts = categoryHierarchy.split("/");
                categoryStr = parts[parts.length - 1];
            }
            dto.setCategory(categoryStr);
            dto.setStock(catalogItem.getStock());
            // Preserve status as stored (fall back to ACTIVE if missing)
            dto.setStatus(catalogItem.getStatus() != null ? catalogItem.getStatus() : ProductResponseDto.ProductStatus.ACTIVE.name());
            dto.setImageUrl(catalogItem.getImageUrl());
            dto.setRegion(catalogItem.getRegion());
            return dto;
        });
    }

    @Override
    public ProductResponseDto update(String id, ProductDto dto) {
        if (dto.getProductId() == null) {
            dto.setProductId(id);
        } else if (!id.equals(dto.getProductId())) {
            throw new ShoppingCartException(ErrorCode.INVALID_INPUT, "Product ID in path does not match ID in body.");
        }
        if (dto.getRegion() == null) {
            dto.setRegion(AppConstants.Pricing.DEFAULT_REGION);
        }
        return updateProduct(dto);
    }

    @Override
    public void delete(String id) {
        deleteProduct(id, AppConstants.Pricing.DEFAULT_REGION);
    }
}