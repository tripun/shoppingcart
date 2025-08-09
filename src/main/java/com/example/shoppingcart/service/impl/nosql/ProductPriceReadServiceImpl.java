package com.example.shoppingcart.service.impl.nosql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.shoppingcart.model.ProductPrice; // Corrected import
import com.example.shoppingcart.repository.ProductPriceRepository; // Use existing repository
import com.example.shoppingcart.service.ProductPriceReadService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("productPriceReadService")
@RequiredArgsConstructor
public class ProductPriceReadServiceImpl implements ProductPriceReadService {

    private static final Logger log = LoggerFactory.getLogger(ProductPriceReadServiceImpl.class);

    private final ProductPriceRepository productPriceReadRepository; // Use existing repository

    @Override
    public Optional<ProductPrice> getProductPriceById(String productId, String region) { // Added region
        String defaultCurrency = "GBP";
        return productPriceReadRepository.findPrice(productId, region, defaultCurrency);
    }

    @Override
    public BigDecimal calculateEffectivePrice(String productId, String region) { // Added region
    return getProductPriceById(productId, region) // Used region
        .map(pp -> new BigDecimal(pp.getPriceInSmallestUnit()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }
}