package com.recipes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "recipe_ingredients",
    uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "ingredient_id"}),
    indexes = {
        @Index(name = "idx_ri_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_ri_ingredient_id", columnList = "ingredient_id")
    }
)
@Getter
@Setter
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @DecimalMin("0.01")
    @Column(nullable = false)
    private Double quantity;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String unit;

    public RecipeIngredient() {}
}
