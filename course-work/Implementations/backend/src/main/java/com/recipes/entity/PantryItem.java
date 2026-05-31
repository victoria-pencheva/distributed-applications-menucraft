package com.recipes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "pantry_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ingredient_id"})
)
@Getter
@Setter
public class PantryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PantryItem() {}

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
