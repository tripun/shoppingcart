package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String id;

    private String name;

    @Column(name = "category_id")
    private Long categoryId;

    private String sku;
    private String description;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at_utc")
    private LocalDateTime createdAtUtc;

    @UpdateTimestamp
    @Column(name = "updated_at_utc")
    private LocalDateTime updatedAtUtc;

    private Long version = 1L;

    @Column(name = "status")
    private String status;

    @Column(name = "image_url")
    private String imageUrl;

    // Explicit getters used by CDC/service code (Lombok @Data would normally generate these)
    // Lombok @Data will generate getters/setters at compile time; avoid manual duplicates
}
