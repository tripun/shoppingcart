package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.repository.CatalogReadRepository;
// replaced PromotionReadRepository with DiscountRuleRepository in this test
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountEdgeCasesTest {

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

    @Spy
    private PriceSanitizer priceSanitizer = new PriceSanitizer(); // use real sanitizer for overflow test

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private CartDto cart;
    private CatalogItem item;

    @BeforeEach
    void setUp() {
        cart = new CartDto();
        CartItemDto cartItem = new CartItemDto();
        cartItem.setProductId("sku-1");
        cart.setItems(Collections.singletonList(cartItem));

        item = new CatalogItem();
        item.setCategoryHierarchy("/CAT/ITEM/");

        // default mocks
    when(inventoryReadService.isInStock(anyString(), anyString(), anyInt())).thenReturn(true);
    when(catalogReadRepository.findById(anyString())).thenReturn(Optional.of(item));
    when(conditionEvaluator.areConditionsMet(any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Buy X Get Y free applies expected discount amount")
    void testBuyXGetYFree() {
        // item price 500 (pence), quantity 3, buy2get1 => free 1 (500)
        item.setPrice(500);
        CartItemDto ci = cart.getItems().get(0);
        ci.setQuantity(3);

    DiscountRule buyXRule = new DiscountRule();
        buyXRule.setIsStackable(false);

    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(any(), any())).thenReturn(Collections.singletonList(buyXRule));
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(Collections.singletonList(buyXRule));

        when(actionApplier.apply(eq(Collections.singletonList(buyXRule)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("buyx", 500)));

        CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");

        assertEquals(500, resp.getTotalDiscount());
        assertEquals(1000, resp.getFinalTotalPrice());
    }

    @Test
    @DisplayName("Three for two rounding behavior with odd prices")
    void testThreeForTwoRounding() {
        // item price 333, qty 3 => one free -> 333 discount
        item.setPrice(333);
        cart.getItems().get(0).setQuantity(3);

    DiscountRule threeForTwo = new DiscountRule();
        threeForTwo.setIsStackable(false);

    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(any(), any())).thenReturn(Collections.singletonList(threeForTwo));
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(Collections.singletonList(threeForTwo));

        when(actionApplier.apply(eq(Collections.singletonList(threeForTwo)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("3for2", 333)));

        CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");

        assertEquals(333, resp.getTotalDiscount());
        assertEquals(666, resp.getFinalTotalPrice());
    }

    @Test
    @DisplayName("Sanitizer scales discounts when they exceed subtotal")
    void testSanitizerScalesOverflow() {
        // item price 200, qty 1 => original 200; action returns 500 discount -> sanitizer should cap to 200
        item.setPrice(200);
        cart.getItems().get(0).setQuantity(1);

    DiscountRule greedy = new DiscountRule();
        greedy.setIsStackable(false);

    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(any(), any())).thenReturn(Collections.singletonList(greedy));
    when(conflictResolver.resolveNonStackable(anyList())).thenReturn(Collections.singletonList(greedy));

        // action reports an over-large discount
        when(actionApplier.apply(eq(Collections.singletonList(greedy)), any())).thenReturn(Collections.singletonList(new AppliedDiscountDto("greedy", 500)));

        CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");

        // sanitizer should cap total discount to original price
        assertEquals(200, resp.getTotalDiscount());
        assertEquals(0, resp.getFinalTotalPrice());
    }
}
