package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.Inventory;
import com.example.shoppingcart.repository.InventoryWriteRepository;
import com.example.shoppingcart.service.InventoryWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryWriteServiceImpl implements InventoryWriteService {

    private static final Logger log = LoggerFactory.getLogger(InventoryWriteServiceImpl.class);

    private final InventoryWriteRepository inventoryWriteRepository;

    @Autowired
    public InventoryWriteServiceImpl(InventoryWriteRepository inventoryWriteRepository) {
        this.inventoryWriteRepository = inventoryWriteRepository;
    }

    @Override
    @Transactional
    public void updateInventory(String productId, String region, int quantityChange) {
        log.info("WRITING to RDBMS: Updating inventory for product {} in region {} by {}",
                productId, region, quantityChange);

        Inventory inventory = inventoryWriteRepository.findByProductIdAndRegion(productId, region);

        if (inventory == null) {
            log.warn("Inventory record not found for product {} in region {}. Creating new record.", productId, region);
            inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setRegion(region);
            inventory.setQuantity(quantityChange);
        } else {
            inventory.setQuantity(inventory.getQuantity() + quantityChange);
        }

        inventoryWriteRepository.save(inventory);
        log.info("Inventory for product {} in region {} updated to quantity {}", productId, region, inventory.getQuantity());
    }
}
