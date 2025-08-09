package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.redis.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This repository handles CRUD operations for the Cart entity in Redis.
@Repository
public interface CartRepository extends CrudRepository<Cart, String> {
}
