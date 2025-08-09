package com.example.shoppingcart.service.impl.nosql;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.shoppingcart.model.ProductInventory;
import com.example.shoppingcart.repository.ProductInventoryRepository;
import com.example.shoppingcart.service.ProductInventoryService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryServiceImpl.class);

    private final ProductInventoryRepository productInventoryRepository;

    @Override
    public Optional<ProductInventory> getProductInventoryById(String productId) {
        String defaultRegion = "UK";
        return productInventoryRepository.findById(productId, defaultRegion);
    }

    @Override
    public ProductInventory createProductInventory(ProductInventory productInventory) {
        productInventoryRepository.save(productInventory);
        return productInventory;
    }

    @Override
    public ProductInventory updateProductInventory(ProductInventory productInventory) {
        String region = productInventory.getRegion() != null ? productInventory.getRegion() : "UK";
        productInventoryRepository.findById(productInventory.getProductId(), region)
                .orElseThrow(() -> new RuntimeException("Product inventory not found for update"));
        productInventoryRepository.save(productInventory);
        return productInventory;
    }

    @Override
    public void deleteProductInventory(String productId) {
        String region = "UK";
        productInventoryRepository.deleteById(productId, region);
    }

    @Override
    public boolean isProductInStock(String productId, int quantity) {
        String region = "UK";
        return productInventoryRepository.findById(productId, region)
                .map(inv -> inv.getQuantity() != null && inv.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    public ProductInventory decreaseStock(String productId, int quantity) {
        String region = "UK";
        ProductInventory inventory = productInventoryRepository.findById(productId, region)
                .orElseThrow(() -> new RuntimeException("Product inventory not found for decrease stock"));
        if (inventory.getQuantity() == null || inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock to decrease");
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        productInventoryRepository.save(inventory);
        return inventory;
    }

    @Override
    public ProductInventory increaseStock(String productId, int quantity) {
        String region = "UK";
        ProductInventory inventory = productInventoryRepository.findById(productId, region)
                .orElseThrow(() -> new RuntimeException("Product inventory not found for increase stock"));
        inventory.setQuantity((inventory.getQuantity() == null ? 0 : inventory.getQuantity()) + quantity);
        productInventoryRepository.save(inventory);
        return inventory;
    }

    // Removed getAll() as it would require a full table scan.
    // @Override
    // public List<ProductInventory> getAll() {
    //     return productInventoryRepository.findAll();
    // }

    @Override
    public ProductInventory create(ProductInventory dto) {
        return createProductInventory(dto);
    }

    @Override
    public Optional<ProductInventory> getById(String id) {
        return getProductInventoryById(id);
    }

    @Override
    public ProductInventory update(String id, ProductInventory dto) {
        return updateProductInventory(dto);
    }

    @Override
    public void delete(String id) {
        deleteProductInventory(id);
    }
}