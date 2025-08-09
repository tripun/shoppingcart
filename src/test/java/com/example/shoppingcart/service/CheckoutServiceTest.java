package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.repository.CatalogReadRepository;
import com.example.shoppingcart.repository.PromotionReadRepository;
import com.example.shoppingcart.service.discount.ActionApplier;
import com.example.shoppingcart.service.discount.ConditionEvaluator;
import com.example.shoppingcart.service.discount.ConflictResolver;
import com.example.shoppingcart.service.discount.PriceSanitizer;
import com.example.shoppingcart.service.impl.CheckoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private com.example.shoppingcart.repository.DiscountRuleRepository discountRuleRepository;
    @Mock
    private CatalogReadRepository catalogReadRepository;
    @Mock
    private InventoryReadService inventoryReadService;
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

    private CartDto cart;
    private CatalogItem item1;

    @BeforeEach
    void setUp() {
        cart = new CartDto();
        CartItemDto cartItem = new CartItemDto();
        cartItem.setProductId("item-1");
        cartItem.setQuantity(1);
        cart.setItems(Collections.singletonList(cartItem));

        item1 = new CatalogItem();
        item1.setPrice(1000);
        item1.setCategoryHierarchy("/CAT1/");

        // By default, mock the sanitizer to return the object it receives.
        when(priceSanitizer.sanitize(any(CheckoutResponseDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when an item is out of stock")
    void testOutOfStock_ShouldThrowException() {
    when(inventoryReadService.isInStock("item-1", "UK", 1)).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
            checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when a product ID is invalid")
    void testInvalidProduct_ShouldThrowException() {
    when(inventoryReadService.isInStock(anyString(), anyString(), anyInt())).thenReturn(true);
    when(catalogReadRepository.findById("item-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK"));
    }

    @Test
    @DisplayName("Should select stackable discounts when their combined value is greater")
    void testBestOutcome_StackableIsBetter() {
    DiscountRule stackableRule = new DiscountRule();
    stackableRule.setIsStackable(true);
    DiscountRule nonStackableRule = new DiscountRule();
    nonStackableRule.setIsStackable(false);

        setupCommonMocks();
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Arrays.asList(stackableRule, nonStackableRule));
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(Collections.singletonList(nonStackableRule));

        // Mock the calculated values for each potential discount combination
    when(actionApplier.apply(eq(Collections.singletonList(stackableRule)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("stackable", 150)));
    when(actionApplier.apply(eq(Collections.singletonList(nonStackableRule)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("non-stackable", 100)));

        checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK");

        // Assert: Verify that the ActionApplier was ultimately called with the stackable rule set, as it was the better value.
        verify(actionApplier, times(1)).apply(eq(Collections.singletonList(stackableRule)), any());
    }

    @Test
    @DisplayName("Should select the single best non-stackable discount when its value is greater")
    void testBestOutcome_NonStackableIsBetter() {
    DiscountRule stackableRule = new DiscountRule();
        stackableRule.setIsStackable(true);
    DiscountRule nonStackableRule = new DiscountRule();
        nonStackableRule.setIsStackable(false);

        setupCommonMocks();
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Arrays.asList(stackableRule, nonStackableRule));
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(Collections.singletonList(nonStackableRule));

    when(actionApplier.apply(eq(Collections.singletonList(stackableRule)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("stackable", 150)));
    when(actionApplier.apply(eq(Collections.singletonList(nonStackableRule)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("non-stackable", 200)));

        checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK");

        // Assert: Verify that the ActionApplier was ultimately called with the non-stackable rule, as it was the better value.
        verify(actionApplier, times(1)).apply(eq(Collections.singletonList(nonStackableRule)), any());
    }

    @Test
    @DisplayName("Should not apply a discount if its conditions are not met")
    void testConditionNotMet_ShouldNotApplyDiscount() {
    DiscountRule rule = new DiscountRule();
        setupCommonMocks();
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Collections.singletonList(rule));
    when(conditionEvaluator.areConditionsMet(eq(rule), any())).thenReturn(false);

        CheckoutResponseDto response = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK");

        assertEquals(0, response.getTotalDiscount());
    verify(conflictResolver, never()).resolveNonStackable(anyList());
    verify(actionApplier, never()).apply(anyList(), any());
    }

    @Test
    @DisplayName("Should calculate correct total when no discounts are applicable")
    void testFinalPriceCalculation_NoDiscounts() {
        setupCommonMocks();
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Collections.emptyList());

        CheckoutResponseDto response = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "credit_card", "UK");

        assertEquals(1000, response.getOriginalTotalPrice());
        assertEquals(0, response.getTotalDiscount());
        assertEquals(1000, response.getFinalTotalPrice());
    verify(priceSanitizer, times(1)).sanitize(any(com.example.shoppingcart.dto.CheckoutResponseDto.class));
    }

    private void setupCommonMocks() {
    when(inventoryReadService.isInStock(anyString(), anyString(), anyInt())).thenReturn(true);
    when(catalogReadRepository.findById(anyString())).thenReturn(Optional.of(item1));
        // Default to conditions being met unless a specific test overrides it.
        when(conditionEvaluator.areConditionsMet(any(), any())).thenReturn(true);
    }
}
