package com.recipes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "shopping_items")
@Getter
@Setter
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private String name;

    private Double quantity;

    private String unit;

    @Column(nullable = false, columnDefinition = "boolean not null default false")
    private boolean checked = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ShoppingItem() {}

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
