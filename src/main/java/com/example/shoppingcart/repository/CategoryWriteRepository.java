package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.postgres.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This repository handles WRITE operations to the RDBMS source of truth for Categories.
@Repository
public interface CategoryWriteRepository extends JpaRepository<Category, String> {
}
