package com.recipes.dto;
import com.recipes.entity.Difficulty;
import jakarta.validation.constraints.*;
public record RecipeRequest(
    @NotBlank @Size(max=100) String name,
    String description,
    @Min(0) int prepTime,
    @Min(0) int cookTime,
    @Min(1) int servings,
    @NotNull Difficulty difficulty,
    String imageUrl,
    Long categoryId,
    String cuisine,
    String blurb,
    Double protein,
    Double carbs,
    Double fat,
    Double fiber) {}
