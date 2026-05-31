package com.recipes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipe_steps", indexes = @Index(columnList = "recipe_id"))
@Getter
@Setter
public class RecipeStep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Min(1)
    @Column(nullable = false)
    private int stepNumber;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public RecipeStep() {}
}
