package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(name = "product_prices")
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "price_in_smallest_unit", nullable = false)
    private Integer priceInSmallestUnit;

    @CreationTimestamp
    @Column(name = "effective_from_utc", nullable = false)
    private LocalDateTime effectiveFromUtc;

    @Version
    private Long version = 1L;

    // Explicit getters used by CDC
    public String getProductId() { return this.productId; }
    public String getRegion() { return this.region; }
    public String getCurrency() { return this.currency; }
    public Integer getPriceInSmallestUnit() { return this.priceInSmallestUnit; }
}