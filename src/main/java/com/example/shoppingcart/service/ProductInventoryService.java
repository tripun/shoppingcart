package com.example.shoppingcart.service;

import java.util.Optional;

import com.example.shoppingcart.model.ProductInventory;
import com.example.shoppingcart.service.crud.CrudService;

/**
 * Service interface for product inventory management operations.
 * Uses the domain DTO {@link ProductInventory} rather than persistence-specific types.
 */
public interface ProductInventoryService extends CrudService<ProductInventory, String, ProductInventory> {

    Optional<ProductInventory> getProductInventoryById(String productId);

    ProductInventory createProductInventory(ProductInventory productInventory);

    ProductInventory updateProductInventory(ProductInventory productInventory);

    void deleteProductInventory(String productId);

    boolean isProductInStock(String productId, int quantity);

    ProductInventory decreaseStock(String productId, int quantity);

    ProductInventory increaseStock(String productId, int quantity);

}
