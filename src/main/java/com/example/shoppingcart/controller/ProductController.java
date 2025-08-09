package com.example.shoppingcart.controller;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.repository.CatalogReadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final CatalogReadRepository catalogReadRepository;

    @Autowired
    public ProductController(CatalogReadRepository catalogReadRepository) {
        this.catalogReadRepository = catalogReadRepository;
    }

    @Operation(summary = "Get a paginated list of products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CatalogItem>> getProductsByCategory(
            @RequestHeader(value = "X-Region", defaultValue = "UK") String region,
            @RequestParam String category,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching products for category: {} in region: {}", category, region);
    String categoryHierarchy = "/" + category.toUpperCase() + "/";
    List<CatalogItem> items = catalogReadRepository.findByCategory(categoryHierarchy, limit);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get the details for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found in the specified region"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/details")
    public ResponseEntity<CatalogItem> getProductDetails(@PathVariable String id,
                                                         @RequestHeader(value = "X-Region", defaultValue = "UK") String region) {
    log.info("Fetching details for product: {} in region: {}", id, region);
    return catalogReadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
