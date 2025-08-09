package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.Product;
import com.example.shoppingcart.repository.ProductWriteRepository;
import com.example.shoppingcart.service.ProductWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductWriteServiceImpl implements ProductWriteService {

    private final ProductWriteRepository productWriteRepository;

    @Autowired
    public ProductWriteServiceImpl(ProductWriteRepository productWriteRepository) {
        this.productWriteRepository = productWriteRepository;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductWriteServiceImpl.class);

    @Override
    public Product createOrUpdateProduct(Product product) {
        log.info("WRITING to RDBMS: Creating/updating product with ID: {}", product.getId());
        // In a real app, you would have more complex logic, DTO mapping, etc.
        return productWriteRepository.save(product);
    }
}
