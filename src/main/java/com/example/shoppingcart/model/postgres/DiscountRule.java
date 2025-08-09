package com.example.shoppingcart.model.postgres;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "discount_rules")
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "valid_from_utc")
    private LocalDateTime validFromUtc;

    @Column(name = "valid_until_utc")
    private LocalDateTime validUntilUtc;

    private Integer priority = 1;

    @Version
    private Long version = 1L;

    public Long getId() { return this.id; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public boolean isActive() { return this.isActive; }
    public LocalDateTime getValidFromUtc() { return this.validFromUtc; }
    public LocalDateTime getValidUntilUtc() { return this.validUntilUtc; }
    public Integer getPriority() { return this.priority; }
}
