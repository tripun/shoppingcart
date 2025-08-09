package com.example.shoppingcart;

import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.service.DiscountService;
import com.example.shoppingcart.service.impl.PriceCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceCalculationServiceTest {

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private PriceCalculationServiceImpl priceCalculationService;

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        cart.setRegion("UK");
        cart.setCurrency("GBP");
        when(discountService.calculateTotalDiscount(any(ShoppingCart.class))).thenReturn(BigDecimal.ZERO);
    }

    @Test
    void testApplesPricing() {
        ShoppingCart.CartItemData item = new ShoppingCart.CartItemData();
        item.setProductId("APPLE");
        item.setQuantity(3);
        item.setPrice(new BigDecimal("0.35"));
        cart.setItems(List.of(item));

        priceCalculationService.calculateCartTotals(cart);
        assertEquals(new BigDecimal("1.05"), cart.getTotal());
    }
}