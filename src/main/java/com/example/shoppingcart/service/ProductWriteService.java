package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.Product;

public interface ProductWriteService {

    /**
     * Creates a new product or updates an existing one in the master RDBMS.
     *
     * @param product The product entity to save.
     * @return The saved product entity.
     */
    Product createOrUpdateProduct(Product product);
}
