package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.AddItemRequest;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.redis.Cart;
import com.example.shoppingcart.repository.CatalogReadRepository;
import com.example.shoppingcart.service.CartReadService;
import com.example.shoppingcart.service.CartWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartWriteService cartWriteService;
    private final CartReadService cartReadService;
    private final CatalogReadRepository catalogReadRepository;

    @Autowired
    public CartController(CartWriteService cartWriteService, CartReadService cartReadService, CatalogReadRepository catalogReadRepository) {
        this.cartWriteService = cartWriteService;
        this.cartReadService = cartReadService;
        this.catalogReadRepository = catalogReadRepository;
    }

    @Operation(summary = "Get a user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access this cart"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.username or hasRole('ADMIN')")
    public Cart getCart(@PathVariable String userId) {
        log.info("Fetching cart for user: {}", userId);
        return cartReadService.getCart(userId);
    }

    @Operation(summary = "Add an item to a user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, e.g., item out of stock or invalid quantity"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access this cart"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#userId == authentication.principal.username or hasRole('ADMIN')")
    public Cart addItemToCart(@PathVariable String userId,
                              @Valid @RequestBody AddItemRequest request,
                              @RequestHeader(value = "X-Region", defaultValue = "UK") String region) {
        log.info("Adding item {} to cart for user: {}", request.getProductId(), userId);
    CatalogItem catalogItem = catalogReadRepository.findById(request.getProductId())
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return cartWriteService.addItem(userId, request.getProductId(), request.getQuantity(), catalogItem.getPrice());
    }

    @Operation(summary = "Update an item's quantity in a user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, e.g., item not in cart"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access this cart"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#userId == authentication.principal.username or hasRole('ADMIN')")
    public Cart updateItemQuantity(@PathVariable String userId,
                                   @PathVariable String productId,
                                   @RequestParam int quantity) {
        log.info("Updating item {} to quantity {} for user: {}", productId, quantity, userId);
        return cartWriteService.updateItemQuantity(userId, productId, quantity);
    }
}