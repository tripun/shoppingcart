package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.ShoppingCart; // Corrected import
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
}