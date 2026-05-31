package com.recipes.dto;

import com.recipes.entity.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record RecipeFullUpdateRequest(
    @NotBlank @Size(max = 100) String name,
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
    Double fiber,
    @Valid List<RecipeIngredientRequest> ingredients,
    @Valid List<RecipeStepRequest> steps
) {}
