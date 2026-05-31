package com.recipes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Min(0)
    @Column(nullable = false)
    private int prepTime;

    @Min(0)
    @Column(nullable = false)
    private int cookTime;

    @Min(1)
    @Column(nullable = false)
    private int servings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    private String imageUrl;

    @Column(length = 50)
    private String cuisine;

    @Column(columnDefinition = "TEXT")
    private String blurb;

    private Double protein;

    private Double carbs;

    private Double fat;

    private Double fiber;

    @Formula("prep_time + cook_time")
    private Integer totalTime;

    @Formula("(SELECT COALESCE(SUM(CASE ri.unit WHEN 'kg' THEN i.calories * ri.quantity * 10.0 WHEN 'pcs' THEN i.calories * ri.quantity ELSE i.calories * ri.quantity / 100.0 END), 0) / GREATEST({alias}.servings, 1) FROM recipe_ingredients ri JOIN ingredients i ON ri.ingredient_id = i.id WHERE ri.recipe_id = {alias}.id)")
    private Double computedCaloriesPerServing;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private java.util.List<RecipeStep> steps = new java.util.ArrayList<>();

    public Recipe() {}

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
