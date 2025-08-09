// package com.example.shoppingcart.service;

// import com.example.shoppingcart.model.Product;
// import com.example.shoppingcart.model.ShoppingCart;
// import com.example.shoppingcart.repository.CartRepository;
// import com.example.shoppingcart.repository.ProductRepository;
// import com.example.shoppingcart.service.impl.CartServiceImpl;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.kafka.core.KafkaTemplate;

// import java.math.BigDecimal;
// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// /**
//  * Comprehensive test cases for CartService covering all scenarios and edge cases.
//  * Target: 95% code coverage with thorough testing.
//  */
// @ExtendWith(MockitoExtension.class)
// class CartServiceTest {

//     @Mock
//     private CartRepository cartRepository;

//     @Mock
//     private ProductRepository productRepository;

//     @Mock
//     private PriceCalculationService priceCalculationService;

//     @Mock
//     private KafkaTemplate<String, Object> kafkaTemplate;

//     @InjectMocks
//     private CartServiceImpl cartService; // Changed to CartServiceImpl to allow direct injection

//     private Product testProduct;
//     private ShoppingCart testCart;

//     @BeforeEach
//     void setUp() {
//         // Setup test product
//         testProduct = new Product();
//         testProduct.setProductId("APPLE");
//         testProduct.setRegion("UK");
//         testProduct.setName("Apple");
//         testProduct.setCategory(Product.ProductCategory.FRUITS);
//         testProduct.setSku("FRUIT-001");
//         testProduct.setPriceInPence(35);
//         testProduct.setStock(100);
//         testProduct.setStatus(Product.ProductStatus.ACTIVE);
//         testProduct.setCurrency("GBP");
//         testProduct.setCreatedAt(LocalDateTime.now());

//         // Setup test cart
//         testCart = new ShoppingCart();
//         testCart.setCartId("CART-12345678");
//         testCart.setUserId("user123");
//         testCart.setRegion("UK");
//         testCart.setCurrency("GBP");
//         testCart.setStatus(ShoppingCart.CartStatus.ACTIVE);
//         testCart.setItems(new ArrayList<>());
//         testCart.setAppliedDiscounts(new HashMap<>());
//         testCart.setCreatedAt(Instant.now());

//         doNothing().when(priceCalculationService).calculateCartTotals(any(ShoppingCart.class));
//     }

//     @Test
//     void createCart_ShouldCreateNewCart_WhenValidUserId() {
//         // Given
//         String userId = "user123";
//         when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

//         // When
//         ShoppingCart result = cartService.createCart(userId);

//         // Then
//         assertNotNull(result);
//         assertEquals(userId, result.getUserId());
//         assertEquals(ShoppingCart.CartStatus.ACTIVE, result.getStatus());
//         verify(cartRepository).save(any(ShoppingCart.class));
//         verify(kafkaTemplate).send(eq("cart-events"), any(String.class), any(ShoppingCart.class)); // Updated to match CartServiceImpl
//     }

//     @Test
//     void addItem_ShouldAddNewItem_WhenItemNotInCart() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "APPLE";
//         int quantity = 2;

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(productId, testCart.getRegion())).thenReturn(Optional.of(testProduct));
//         when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

//         // When
//         ShoppingCart result = cartService.addItem(cartId, productId, quantity);

//         // Then
//         assertNotNull(result);
//         assertEquals(1, result.getItems().size());
//         assertEquals(productId, result.getItems().get(0).getProductId());
//         assertEquals(quantity, result.getItems().get(0).getQuantity());
//         verify(cartRepository).save(any(ShoppingCart.class));
//         verify(kafkaTemplate).send(eq("cart-events"), any(String.class), any(ShoppingCart.class)); // Updated to match CartServiceImpl
//     }

//     @Test
//     void addItem_ShouldUpdateQuantity_WhenItemAlreadyInCart() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "APPLE";
//         int quantity = 2;
//         int existingQuantity = 3;

//         // Setup existing item in cart
//         ShoppingCart.CartItemData existingItem = new ShoppingCart.CartItemData();
//         existingItem.setProductId(productId);
//         existingItem.setQuantity(existingQuantity);
//         testCart.getItems().add(existingItem);

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(productId, testCart.getRegion())).thenReturn(Optional.of(testProduct));
//         when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

//         // When
//         ShoppingCart result = cartService.addItem(cartId, productId, quantity);

//         // Then
//         assertNotNull(result);
//         assertEquals(1, result.getItems().size());
//         assertEquals(existingQuantity + quantity, result.getItems().get(0).getQuantity());
//     }

//     @Test
//     void addItem_ShouldThrowException_WhenInsufficientStock() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "APPLE";
//         int quantity = 150; // More than available stock

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(productId, testCart.getRegion())).thenReturn(Optional.of(testProduct));

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class,
//             () -> cartService.addItem(cartId, productId, quantity));

//         assertTrue(exception.getMessage().contains("Insufficient stock"));
//     }

//     @Test
//     void addItem_ShouldThrowException_WhenProductNotFound() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "NONEXISTENT";
//         int quantity = 2;

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(productId, testCart.getRegion())).thenReturn(Optional.empty());

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class,
//             () -> cartService.addItem(cartId, productId, quantity));

//         assertTrue(exception.getMessage().contains("Product not found"));
//     }

//     @Test
//     void removeItem_ShouldRemoveItem_WhenItemExists() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "APPLE";

//         // Setup existing item in cart
//         ShoppingCart.CartItemData existingItem = new ShoppingCart.CartItemData();
//         existingItem.setProductId(productId);
//         existingItem.setQuantity(2);
//         testCart.getItems().add(existingItem);

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

//         // When
//         ShoppingCart result = cartService.removeItem(cartId, productId);

//         // Then
//         assertNotNull(result);
//         assertEquals(0, result.getItems().size());
//         verify(cartRepository).save(any(ShoppingCart.class));
//         verify(kafkaTemplate).send(eq("cart-events"), any(String.class), any(ShoppingCart.class));
//     }

//     @Test
//     void removeItem_ShouldThrowException_WhenItemNotFound() {
//         // Given
//         String cartId = "CART-12345678";
//         String productId = "NONEXISTENT";

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class,
//             () -> cartService.removeItem(cartId, productId));

//         assertTrue(exception.getMessage().contains("Item not found in cart"));
//     }

//     @Test
//     void getCart_ShouldReturnCart_WhenCartExists() {
//         // Given
//         String cartId = "CART-12345678";
//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

//         // When
//         ShoppingCart result = cartService.getCart(cartId);

//         // Then
//         assertNotNull(result);
//         assertEquals(cartId, result.getCartId());
//         verify(cartRepository).findById(cartId);
//     }

//     @Test
//     void getCart_ShouldThrowException_WhenCartNotFound() {
//         // Given
//         String cartId = "NONEXISTENT";
//         when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class,
//             () -> cartService.getCart(cartId));

//         assertTrue(exception.getMessage().contains("Cart not found"));
//     }

//     @Test
//     void calculateTotal_ShouldReturnCorrectTotal() {
//         // Given
//         String cartId = "CART-12345678";
//         testCart.setSubtotal(new BigDecimal("3.50")); // Set subtotal directly
//         testCart.setTotal(new BigDecimal("3.50")); // Set total directly

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

//         // When
//         BigDecimal result = cartService.calculateTotal(cartId);

//         // Then
//         assertEquals(new BigDecimal("3.50"), result);
//     }

//     @Test
//     void checkout_ShouldCompleteCheckout_WhenCartHasItems() {
//         // Given
//         String cartId = "CART-12345678";

//         // Setup cart with items
//         ShoppingCart.CartItemData item = new ShoppingCart.CartItemData();
//         item.setProductId("APPLE");
//         item.setQuantity(2);
//         testCart.getItems().add(item);

//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(eq("APPLE"), eq(testCart.getRegion()))).thenReturn(Optional.of(testProduct));
//         doAnswer(invocation -> invocation.getArgument(0)).when(productRepository).save(any(Product.class));
//         when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

//         // When
//         ShoppingCart result = cartService.checkout(cartId);

//         // Then
//         assertNotNull(result);
//         assertEquals(ShoppingCart.CartStatus.CHECKED_OUT, result.getStatus());
        
//         verify(cartRepository).save(any(ShoppingCart.class));
//         verify(kafkaTemplate).send(eq("cart-events"), any(String.class), any(ShoppingCart.class));
//     }

//     @Test
//     void checkout_ShouldThrowException_WhenCartIsEmpty() {
//         // Given
//         String cartId = "CART-12345678";
//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class,
//             () -> cartService.checkout(cartId));

//         assertTrue(exception.getMessage().contains("Cannot checkout empty cart"));
//     }

//     // @Test
//     // void processDirectCheckout_ShouldProcessSuccessfully_WithValidProducts() {
//     //     // Given
//     //     List<String> productNames = Arrays.asList("Apple", "Apple", "Banana");

//     //     // Mock product lookup
//     //     Product bananaProduct = new Product();
//     //     bananaProduct.setProductId("BANANA");
//     //     bananaProduct.setName("Banana");
//     //     bananaProduct.setPriceInPence(20);
//     //     bananaProduct.setStock(50);

//     //     when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
//     //     when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, bananaProduct));
//     //     when(productRepository.findByProductId(anyString())).thenReturn(Optional.of(testProduct));

//     //     // When
//     //     ShoppingCart result = cartService.processDirectCheckout(productNames);

//     //     // Then
//     //     assertNotNull(result);
//     //     assertEquals("CHECKED_OUT", result.getStatus());
//     //     verify(cartRepository, atLeastOnce()).save(any(ShoppingCart.class));
//     // }

//     // @Test
//     // void processDirectCheckout_ShouldThrowException_WhenProductNotFound() {
//     //     // Given
//     //     List<String> productNames = Arrays.asList("NonexistentProduct");

//     //     when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
//     //     when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

//     //     // When & Then
//     //     RuntimeException exception = assertThrows(RuntimeException.class,
//     //         () -> cartService.processDirectCheckout(productNames));

//     //     assertTrue(exception.getMessage().contains("Product not found"));
//     // }

//     // @Test
//     // void processDirectCheckout_ShouldHandleDuplicateProducts() {
//     //     // Given
//     //     List<String> productNames = Arrays.asList("Apple", "Apple", "Apple");

//     //     when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
//     //     when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));
//     //     when(productRepository.findByProductId("APPLE")).thenReturn(Optional.of(testProduct));

//     //     // When
//     //     ShoppingCart result = cartService.processDirectCheckout(productNames);

//     //     // Then
//     //     assertNotNull(result);
//     //     verify(cartRepository, atLeastOnce()).save(any(ShoppingCart.class));
//     // }

//     @Test
//     void kafkaEventPublishing_ShouldHandleFailures_Gracefully() {
//         // Given
//         String cartId = "CART-12345678";
//         when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
//         when(productRepository.findByProductIdAndRegion(eq("APPLE"), eq(testCart.getRegion()))).thenReturn(Optional.of(testProduct));

//         // Simulate Kafka failure
//         doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(anyString(), any());

//         // When
//         ShoppingCart result = cartService.addItem(cartId, "APPLE", 1);

//         // Then
//         assertNotNull(result); // Operation should still complete
//         verify(kafkaTemplate).send(eq("cart-events"), any(String.class), any(ShoppingCart.class));
//     }
// }