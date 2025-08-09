package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "product_inventories") // Corrected table name
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming auto-increment
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Version // Optimistic locking
    private Long version = 1L;

    // Explicit getters used by CDC
    public String getProductId() { return this.productId; }
    public String getRegion() { return this.region; }
    public int getQuantity() { return this.quantity; }
}
