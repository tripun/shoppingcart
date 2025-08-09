package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.discount.ActionApplier;
import com.example.shoppingcart.service.discount.ConditionEvaluator;
import com.example.shoppingcart.service.discount.ConflictResolver;
import com.example.shoppingcart.service.discount.PriceSanitizer;
import com.example.shoppingcart.service.impl.CheckoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CheckoutServiceUnitTest {

    @Mock
    private com.example.shoppingcart.repository.DiscountRuleRepository discountRuleRepository;
    @Mock
    private com.example.shoppingcart.repository.CatalogReadRepository catalogReadRepository;
    @Mock
    private com.example.shoppingcart.service.InventoryReadService inventoryReadService;
    @Mock
    private com.example.shoppingcart.service.InventoryWriteService inventoryWriteService;
    @Mock
    private com.example.shoppingcart.service.OrderService orderService;
    @Mock
    private ConditionEvaluator conditionEvaluator;
    @Mock
    private ConflictResolver conflictResolver;
    @Mock
    private ActionApplier actionApplier;
    @Mock
    private PriceSanitizer priceSanitizer;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkout_appliesDiscountsAndCreatesOrder() {
        // Arrange
        CartItemDto item = new CartItemDto();
        item.setProductId("P1");
        item.setQuantity(2);
        CartDto cart = new CartDto();
        cart.setItems(List.of(item));
        cart.setCurrency("GBP");
        cart.setCartId("C1");
        cart.setUserId("user1");

        CatalogItem ci = new CatalogItem();
        ci.setPk("PRODUCT#P1");
        ci.setSk("REGION#UK");
        ci.setName("Apple");
        ci.setPrice(100);
        ci.setCategoryHierarchy("/FOOD/FRUIT/");

    when(inventoryReadService.isInStock("P1", "UK", 2)).thenReturn(true);
    when(catalogReadRepository.findById("P1")).thenReturn(Optional.of(ci));
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Collections.emptyList());
    when(actionApplier.apply(anyList(), any())).thenReturn(Collections.emptyList());
    when(priceSanitizer.sanitize(any(com.example.shoppingcart.dto.CheckoutResponseDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderService.createOrder(any(com.example.shoppingcart.model.postgres.Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK");

        // Assert
        assertNotNull(resp);
        assertEquals(200, resp.getOriginalTotalPrice());
        assertEquals(0, resp.getTotalDiscount());
        assertEquals(200, resp.getFinalTotalPrice());
    verify(orderService, times(1)).createOrder(any(com.example.shoppingcart.model.postgres.Order.class));
        verify(inventoryWriteService, times(1)).updateInventory("P1", "UK", -2);
    }
}
