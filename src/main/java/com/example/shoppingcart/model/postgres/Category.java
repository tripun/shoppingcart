package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "product_categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "parent_id")
    private Long parentId; // Nullable parent ID for hierarchy

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Version
    private Long version = 1L;
}
