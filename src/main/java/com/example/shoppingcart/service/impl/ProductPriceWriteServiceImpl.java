package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.ProductPrice;
import com.example.shoppingcart.repository.jpa.ProductPriceRepository;
import com.example.shoppingcart.service.ProductPriceWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductPriceWriteServiceImpl implements ProductPriceWriteService {

    private final ProductPriceRepository productPriceRepository;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductPriceWriteServiceImpl.class);

    @Override
    @Transactional
    public ProductPrice createOrUpdateProductPrice(ProductPrice productPrice) {
        log.info("WRITING to RDBMS: Creating/updating product price for product {} in region {} currency {}",
                productPrice.getProductId(), productPrice.getRegion(), productPrice.getCurrency());
        return productPriceRepository.save(productPrice);
    }

    @Override
    @Transactional
    public void deleteProductPrice(Long id) {
        log.info("WRITING to RDBMS: Deleting product price with ID: {}", id);
        productPriceRepository.deleteById(id);
    }
}