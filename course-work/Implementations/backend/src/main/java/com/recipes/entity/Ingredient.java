package com.recipes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients", uniqueConstraints =
    @UniqueConstraint(name = "uk_ingredient_name_unit", columnNames = {"name", "default_unit"}))
@Getter
@Setter
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @NotBlank
    @Pattern(regexp = "^(g|kg|pcs)$", message = "Unit must be g, kg or pcs")
    @Column(name = "default_unit", nullable = false, length = 10)
    private String defaultUnit;

    @DecimalMin("0.0")
    private Double calories;

    @DecimalMin("0.0")
    private Double protein;

    @DecimalMin("0.0")
    private Double carbs;

    @DecimalMin("0.0")
    private Double fat;

    @DecimalMin("0.0")
    private Double fiber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IngredientCategory category;

    @Column(nullable = false, columnDefinition = "boolean not null default false")
    private boolean containsGluten = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Ingredient() {}

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
