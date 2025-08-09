package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.discount.ActionApplier;
import com.example.shoppingcart.service.discount.ConditionEvaluator;
import com.example.shoppingcart.service.discount.ConflictResolver;
import com.example.shoppingcart.service.impl.CheckoutServiceImpl;
import com.example.shoppingcart.service.discount.PriceSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class DiscountEngineUnitTest {

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
    void discountFlow_fetchEvaluateResolveApplySanitize() {
        CartItemDto item = new CartItemDto();
        item.setProductId("P1");
        item.setQuantity(1);
        CartDto cart = new CartDto();
        cart.setItems(List.of(item));
        cart.setCurrency("GBP");
        cart.setCartId("C1");
        cart.setUserId("user1");

        CatalogItem ci = new CatalogItem();
        ci.setPk("PRODUCT#P1");
        ci.setSk("REGION#UK");
        ci.setName("Orange");
        ci.setPrice(150);

    when(inventoryReadService.isInStock(anyString(), anyString(), anyInt())).thenReturn(true);
    when(catalogReadRepository.findById("P1")).thenReturn(java.util.Optional.of(ci));

        DiscountRule dr = new DiscountRule();
        dr.setRuleId("R1");
        when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(List.of(dr));
        when(conditionEvaluator.areConditionsMet(eq(dr), any())).thenReturn(true);
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(List.of(dr));

        AppliedDiscountDto applied = new AppliedDiscountDto();
        applied.setRuleId("R1");
        applied.setRuleName("R1");
        applied.setAmount(50);
        when(actionApplier.apply(anyList(), any())).thenReturn(List.of(applied));
        when(priceSanitizer.sanitize(any(com.example.shoppingcart.dto.CheckoutResponseDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderService.createOrder(any(com.example.shoppingcart.model.postgres.Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resp = checkoutService.calculateFinalPrice(cart, java.util.Collections.emptySet(), "credit_card", "UK");

        assertNotNull(resp);
        assertEquals(150, resp.getOriginalTotalPrice());
        assertEquals(50, resp.getTotalDiscount());
        assertEquals(100, resp.getFinalTotalPrice());
    }
}
