package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.config.AppConstants;
import com.example.shoppingcart.exception.ShoppingCartException;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.model.redis.Cart;
import com.example.shoppingcart.repository.ShoppingCartRepository;
import com.example.shoppingcart.repository.CatalogReadRepository;
import com.example.shoppingcart.service.CartWriteService;
import com.example.shoppingcart.service.PriceCalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.shoppingcart.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartWriteServiceImpl implements CartWriteService {

    private static final Logger log = LoggerFactory.getLogger(CartWriteServiceImpl.class);
    private final ShoppingCartRepository cartRepository;
    private final CatalogReadRepository catalogReadRepository;
    private final PriceCalculationService priceCalculationService;

    @Override
    public Cart createCart(String userId) {
        log.info("Creating new cart for user: {}", userId);

        ShoppingCart cart = new ShoppingCart();
        cart.setCartId(generateCartId());
        cart.setUserId(userId);
        cart.setStatus(ShoppingCart.CartStatus.ACTIVE);
        cart.setItems(new ArrayList<>());
        cart.setCreatedAt(Instant.now());
        cart.setUpdatedAt(Instant.now());
        cart.setRegion(AppConstants.Pricing.DEFAULT_REGION);
        cart.setCurrency(AppConstants.Pricing.DEFAULT_CURRENCY);
        cart.setAppliedDiscounts(new HashMap<>());

    ShoppingCart savedCart = cartRepository.save(cart);
    log.info("Created cart with ID: {}", savedCart.getCartId());
    return toRedisCart(savedCart);
    }

    @Override
    @CacheEvict(value = "carts", key = "#cartId")
    public Cart addItem(String cartId, String productId, int quantity, int priceInSmallestUnit) {
        log.info("Adding {} of product {} to cart {}", quantity, productId, cartId);

        ShoppingCart cart = getCart(cartId);
            // Context lines before the change
            // This line retrieves the product from the catalog
            CatalogItem product = catalogReadRepository.findById(productId)
                .orElseThrow(() -> new ShoppingCartException(PROD_100_NOT_FOUND, "Product not found: " + productId));

        Optional<ShoppingCart.CartItemData> existingItemOpt = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();

        if (existingItemOpt.isPresent()) {
            ShoppingCart.CartItemData item = existingItemOpt.get();
            int newQuantity = item.getQuantity() + quantity;
            item.setQuantity(newQuantity);
        } else {
            ShoppingCart.CartItemData newItem = new ShoppingCart.CartItemData(
                product.getPk(),
                quantity,
                new BigDecimal(priceInSmallestUnit),
                new BigDecimal(priceInSmallestUnit).multiply(BigDecimal.valueOf(quantity)),
                product.getName(),
                product.getDescription(),
                null, 
                product.getCategoryHierarchy()
            );
            cart.getItems().add(newItem);
        }

        priceCalculationService.calculateCartTotals(cart);
        cart.setUpdatedAt(Instant.now());

    ShoppingCart saved = cartRepository.save(cart);
    return toRedisCart(saved);
    }

    @Override
    @CacheEvict(value = "carts", key = "#cartId")
    public Cart removeItem(String cartId, String productId) {
        log.info("Removing product {} from cart {}", productId, cartId);

        ShoppingCart cart = getCart(cartId);

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new ShoppingCartException(CART_002_ITEM_NOT_FOUND, "Item not found in cart: " + productId);
        }

        priceCalculationService.calculateCartTotals(cart);
        cart.setUpdatedAt(Instant.now());

    ShoppingCart saved = cartRepository.save(cart);
    return toRedisCart(saved);
    }

    @Override
    @CacheEvict(value = "carts", key = "#cartId")
    public Cart updateItemQuantity(String cartId, String productId, int quantity) {
        log.info("Updating quantity of product {} in cart {} to {}", productId, cartId, quantity);

        if (quantity <= 0) {
            return removeItem(cartId, productId);
        }

        ShoppingCart cart = getCart(cartId);

        ShoppingCart.CartItemData cartItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ShoppingCartException(CART_002_ITEM_NOT_FOUND, "Item not found in cart: " + productId));

        cartItem.setQuantity(quantity);
        priceCalculationService.calculateCartTotals(cart);
        cart.setUpdatedAt(Instant.now());

    ShoppingCart saved = cartRepository.save(cart);
    return toRedisCart(saved);
    }

    @Override
    @CacheEvict(value = {"carts", "cartTotals"}, key = "#cartId")
    public Cart checkout(String cartId) {
        log.info("Processing checkout for cart: {}", cartId);

        ShoppingCart cart = getCart(cartId);

        if (cart.getItems().isEmpty()) {
            throw new ShoppingCartException(CART_005_IS_EMPTY, "Cannot checkout empty cart: " + cartId);
        }

        priceCalculationService.calculateCartTotals(cart);

        cart.setStatus(ShoppingCart.CartStatus.CHECKED_OUT);
        cart.setUpdatedAt(Instant.now());
    ShoppingCart checkedOutCart = cartRepository.save(cart);
    return toRedisCart(checkedOutCart);
    }

    private ShoppingCart getCart(String cartId) {
        log.debug("Retrieving cart: {}", cartId);
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new ShoppingCartException(CART_001_NOT_FOUND, "Cart not found: " + cartId));
    }

    private String generateCartId() {
        return AppConstants.Cart.CART_ID_PREFIX + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // --- Conversion helpers between Dynamo ShoppingCart and Redis Cart ---
    private Cart toRedisCart(ShoppingCart s) {
        Cart c = new Cart();
        c.setCartId(s.getCartId());
        c.setUserId(s.getUserId());
        c.setCurrency(s.getCurrency());
        c.setRegion(s.getRegion());
        c.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        // Convert list of CartItemData to a simple map productId->quantity for Redis model
    Map<String, Integer> itemsMap = new HashMap<>();
        if (s.getItems() != null) {
            for (ShoppingCart.CartItemData item : s.getItems()) {
                itemsMap.put(item.getProductId(), item.getQuantity());
            }
        }
        c.setItems(itemsMap);
        c.setCreatedAt(s.getCreatedAt());
        c.setUpdatedAt(s.getUpdatedAt());
        c.setAppliedDiscounts(s.getAppliedDiscounts());
        return c;
    }
}
