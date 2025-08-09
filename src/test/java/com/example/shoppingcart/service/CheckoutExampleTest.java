package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartDto;
import com.example.shoppingcart.dto.CartItemDto;
import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.repository.CatalogReadRepository;
import com.example.shoppingcart.service.discount.ActionApplier;
import com.example.shoppingcart.service.InventoryReadService;
import com.example.shoppingcart.service.InventoryWriteService;
import com.example.shoppingcart.service.discount.PriceSanitizer;
import com.example.shoppingcart.service.discount.ConditionEvaluator;
import com.example.shoppingcart.service.discount.ConflictResolver;
import com.example.shoppingcart.service.impl.CheckoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckoutExampleTest {

    @Mock
    CatalogReadRepository catalogReadRepository;
    @Mock
    private InventoryReadService inventoryReadService;
    @Mock
    private com.example.shoppingcart.repository.DiscountRuleRepository discountRuleRepository;
    @Mock
    private InventoryWriteService inventoryWriteService;
    @Mock
    private com.example.shoppingcart.service.OrderService orderService;
    @Mock
    private ConditionEvaluator conditionEvaluator;
    @Mock
    private ConflictResolver conflictResolver;
    @Mock
    private PriceSanitizer priceSanitizer;

    // We'll construct a real ActionApplier out of strategies so tests exercise real discount logic
    private com.example.shoppingcart.service.discount.ActionApplier actionApplier;

    private CheckoutServiceImpl checkoutService;

    private CatalogItem apple;
    private CatalogItem banana;
    private CatalogItem melon;
    private CatalogItem lime;

    @BeforeEach
    void setUp() {
        apple = new CatalogItem();
        apple.setPk("APPLE");
        apple.setPrice(35);

        banana = new CatalogItem();
        banana.setPk("BANANA");
        banana.setPrice(20);

        melon = new CatalogItem();
        melon.setPk("MELON");
        melon.setPrice(50);

        lime = new CatalogItem();
        lime.setPk("LIME");
        lime.setPrice(15);

    when(inventoryReadService.isInStock(anyString(), anyString(), anyInt())).thenReturn(true);
        when(catalogReadRepository.findById("APPLE")).thenReturn(Optional.of(apple));
        when(catalogReadRepository.findById("BANANA")).thenReturn(Optional.of(banana));
        when(catalogReadRepository.findById("MELON")).thenReturn(Optional.of(melon));
        when(catalogReadRepository.findById("LIME")).thenReturn(Optional.of(lime));

    // Build real action applier with the available strategies
    actionApplier = new com.example.shoppingcart.service.discount.ActionApplier(
        List.of(
            new com.example.shoppingcart.service.discount.action.BuyXGetYFreeStrategy(),
            new com.example.shoppingcart.service.discount.action.PercentageOffProductStrategy(),
            new com.example.shoppingcart.service.discount.action.PercentageOffCategoryStrategy(),
            new com.example.shoppingcart.service.discount.action.FixedAmountOffCartStrategy(),
            new com.example.shoppingcart.service.discount.action.ApplyFreeShippingStrategy()
        )
    );

    when(priceSanitizer.sanitize(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(conflictResolver.resolveNonStackable(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(conditionEvaluator.areConditionsMet(any(), any())).thenReturn(true);
    // default: no discount rules unless test provides them
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(anyString(), anyList())).thenReturn(Collections.emptyList());
    // inventory write is a no-op in unit tests; order creation should return the created Order (echo)
    doNothing().when(inventoryWriteService).updateInventory(anyString(), anyString(), anyInt());
    when(orderService.createOrder(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // construct CheckoutServiceImpl with mocks and the real actionApplier
    checkoutService = new CheckoutServiceImpl(
        discountRuleRepository,
        catalogReadRepository,
        inventoryReadService,
        inventoryWriteService,
        orderService,
        conditionEvaluator,
        conflictResolver,
        actionApplier,
        priceSanitizer
    );
    }

    private CartDto toCart(String... items) {
        CartDto cart = new CartDto();
        List<CartItemDto> list = Arrays.stream(items).map(id -> {
            CartItemDto it = new CartItemDto(); it.setProductId(id); it.setQuantity(1); return it; }).toList();
        cart.setItems(list);
        cart.setUserId("user1");
        cart.setCartId("cart1");
        cart.setCurrency("GBP");
        return cart;
    }

    @Test
    @DisplayName("Example: Apples & Bananas")
    void example_apples_and_bananas() {
        CartDto cart = toCart("APPLE", "APPLE", "BANANA");
        CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");
        assertEquals(35*2 + 20, resp.getFinalTotalPrice());
    }

    @Test
    @DisplayName("Example: Melon BOGO")
    void example_melon_bogo() {
        CartDto cart = toCart("MELON", "MELON");
    // Provide a DiscountRule for MELON BOGO
    com.example.shoppingcart.model.dynamo.DiscountRule melonRule = new com.example.shoppingcart.model.dynamo.DiscountRule();
    melonRule.setRuleId("PROMOTION#MELON_BOGO");
    melonRule.setIsStackable(false);
    com.example.shoppingcart.model.dynamo.DiscountRule.Action act = new com.example.shoppingcart.model.dynamo.DiscountRule.Action();
    act.setType("BUY_X_GET_Y_FREE");
    act.setProductId("MELON");
    act.setBuyQuantity(1);
    act.setGetQuantity(1);
    melonRule.setActions(List.of(act));
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(eq("MELON"), anyList())).thenReturn(List.of(melonRule));

    CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");
    // Two melons at 50 each with BOGO -> pay for 1 melon = 50
    assertEquals(50, resp.getFinalTotalPrice());
    }

    @Test
    @DisplayName("Example: Lime 3-for-2")
    void example_lime_3_for_2() {
        CartDto cart = toCart("LIME", "LIME", "LIME");
    com.example.shoppingcart.model.dynamo.DiscountRule limeRule = new com.example.shoppingcart.model.dynamo.DiscountRule();
    limeRule.setRuleId("PROMOTION#LIME_3_FOR_2");
    limeRule.setIsStackable(false);
    com.example.shoppingcart.model.dynamo.DiscountRule.Action act = new com.example.shoppingcart.model.dynamo.DiscountRule.Action();
    act.setType("BUY_X_GET_Y_FREE");
    act.setProductId("LIME");
    act.setBuyQuantity(2);
    act.setGetQuantity(1);
    limeRule.setActions(List.of(act));
    when(discountRuleRepository.findActiveRulesByProductAndHierarchy(eq("LIME"), anyList())).thenReturn(List.of(limeRule));

    CheckoutResponseDto resp = checkoutService.calculateFinalPrice(cart, Collections.emptySet(), "card", "UK");
    // Three limes at 15 each with 3-for-2 -> pay for 2 limes = 30
    assertEquals(30, resp.getFinalTotalPrice());
    }
}
