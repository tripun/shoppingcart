package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.postgres.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This repository handles WRITE operations to the RDBMS source of truth.
@Repository
public interface ProductWriteRepository extends JpaRepository<Product, String> {
}
