package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.redis.Cart;
import com.example.shoppingcart.service.CartReadService;
import com.example.shoppingcart.service.CheckoutService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;
    private final CartReadService cartReadService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, CartReadService cartReadService) {
        this.checkoutService = checkoutService;
        this.cartReadService = cartReadService;
    }

    @Operation(summary = "Checkout a user's persistent cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout calculation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input, e.g., cart is empty or item out of stock"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access this cart"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/by-cart/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#userId == authentication.principal.username or hasRole('ADMIN')")
    public CheckoutResponseDto checkoutByCartId(
            @PathVariable String userId,
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-User-Tags", required = false) Set<String> userTags,
            @RequestHeader(value = "X-Region", defaultValue = "UK") String region) {

        log.info("Processing checkout for user {} with Idempotency-Key: {}", userId, idempotencyKey);
        Cart cart = cartReadService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty cart.");
        }
        CartDto cartDto = convertCartToDto(cart);
        return executeCheckout(cartDto, userTags, region);
    }

    @Operation(summary = "Perform a stateless checkout with a list of product IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout calculation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input, e.g., empty list or item out of stock"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/by-list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public CheckoutResponseDto checkoutByList(
            @Valid @RequestBody List<String> productIds,
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-User-Tags", required = false) Set<String> userTags,
            @RequestHeader(value = "X-Region", defaultValue = "UK") String region) {

        log.info("Processing stateless checkout with Idempotency-Key: {}", idempotencyKey);
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty list of items.");
        }
        CartDto cartDto = convertListToDto(productIds);
        return executeCheckout(cartDto, userTags, region);
    }

    private CheckoutResponseDto executeCheckout(CartDto cartDto, Set<String> userTags, String region) {
        Set<String> tags = userTags != null ? userTags : Collections.emptySet();
        String paymentMethod = "credit_card";
        return checkoutService.calculateFinalPrice(cartDto, tags, paymentMethod, region);
    }

    private CartDto convertListToDto(List<String> productIds) {
        Map<String, Long> counts = productIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<CartItemDto> items = counts.entrySet().stream()
                .map(entry -> {
                    CartItemDto itemDto = new CartItemDto();
                    itemDto.setProductId(entry.getKey());
                    itemDto.setQuantity(entry.getValue().intValue());
                    return itemDto;
                })
                .collect(Collectors.toList());
        CartDto cartDto = new CartDto();
        cartDto.setItems(items);
        return cartDto;
    }

    private CartDto convertCartToDto(Cart cart) {
        List<CartItemDto> items = cart.getItems().entrySet().stream()
                .map(entry -> {
                    CartItemDto itemDto = new CartItemDto();
                    itemDto.setProductId(entry.getKey());
                    itemDto.setQuantity(entry.getValue());
                    return itemDto;
                })
                .collect(Collectors.toList());
        CartDto cartDto = new CartDto();
        cartDto.setItems(items);
        return cartDto;
    }
}
