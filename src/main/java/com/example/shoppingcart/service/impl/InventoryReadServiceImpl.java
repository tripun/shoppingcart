package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.repository.InventoryReadRepository;
import com.example.shoppingcart.service.InventoryReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryReadServiceImpl implements InventoryReadService {

    private static final Logger log = LoggerFactory.getLogger(InventoryReadServiceImpl.class);

    private final InventoryReadRepository inventoryReadRepository;

    @Autowired
    public InventoryReadServiceImpl(InventoryReadRepository inventoryReadRepository) {
        this.inventoryReadRepository = inventoryReadRepository;
    }

    @Override
    public boolean isInStock(String productId, String region, int requestedQuantity) {
        log.debug("Reading inventory for product {} in region {}", productId, region);
    return inventoryReadRepository.findById(productId, region)
                .map(record -> record.getIsAvailable() && record.getStockLevel() >= requestedQuantity)
                .orElse(false);
    }
}
