package com.example.shoppingcart.controller;

import com.example.shoppingcart.model.postgres.Product;
import com.example.shoppingcart.model.postgres.DiscountRule;
import com.example.shoppingcart.model.postgres.ProductPrice;
import com.example.shoppingcart.service.InventoryWriteService;
import com.example.shoppingcart.service.ProductWriteService;
import com.example.shoppingcart.service.DiscountRuleWriteService;
import com.example.shoppingcart.service.ProductPriceWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final InventoryWriteService inventoryWriteService;
    private final ProductWriteService productWriteService;
    private final DiscountRuleWriteService discountRuleWriteService;
    private final ProductPriceWriteService productPriceWriteService;

    @Autowired
    public AdminController(InventoryWriteService inventoryWriteService,
                           ProductWriteService productWriteService,
                           DiscountRuleWriteService discountRuleWriteService,
                           ProductPriceWriteService productPriceWriteService) {
        this.inventoryWriteService = inventoryWriteService;
        this.productWriteService = productWriteService;
        this.discountRuleWriteService = discountRuleWriteService;
        this.productPriceWriteService = productPriceWriteService;
    }

    @Operation(summary = "Update inventory stock for a product in a region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not an admin")
    })
    @PostMapping("/inventory/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateInventory(@RequestParam String productId,
                                @RequestParam String region,
                                @RequestParam int quantityChange) {
        log.info("Updating inventory for product {} in region {} by {}", productId, region, quantityChange);
        inventoryWriteService.updateInventory(productId, region, quantityChange);
    }

    @Operation(summary = "Create or update a product in the master catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created/updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data provided"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not an admin")
    })
    @PostMapping("/products")
    public ResponseEntity<Product> createOrUpdateProduct(@Valid @RequestBody Product product) {
        log.info("Creating/updating product: {}", product.getId());
        Product savedProduct = productWriteService.createOrUpdateProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Create or update a discount rule in the master list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discount rule created/updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid discount rule data provided"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not an admin")
    })
    @PostMapping("/discount-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountRule createOrUpdateDiscountRule(@Valid @RequestBody DiscountRule discountRule) {
        log.info("Creating/updating discount rule: {}", discountRule.getId());
        return discountRuleWriteService.createOrUpdateDiscountRule(discountRule);
    }

    @Operation(summary = "Create or update a product price in the master list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product price created/updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product price data provided"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not an admin")
    })
    @PostMapping("/prices")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductPrice createOrUpdateProductPrice(@Valid @RequestBody ProductPrice productPrice) {
        log.info("Creating/updating product price for product {} in region {} currency {}",
                productPrice.getProductId(), productPrice.getRegion(), productPrice.getCurrency());
        return productPriceWriteService.createOrUpdateProductPrice(productPrice);
    }

    @Operation(summary = "Delete a product price from the master list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product price deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product price not found"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not an admin")
    })
    @DeleteMapping("/prices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductPrice(@PathVariable Long id) {
        log.info("Deleting product price with ID: {}", id);
        productPriceWriteService.deleteProductPrice(id);
    }
}