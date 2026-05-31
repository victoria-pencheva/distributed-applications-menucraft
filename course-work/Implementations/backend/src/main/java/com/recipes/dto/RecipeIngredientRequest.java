package com.recipes.dto;
import jakarta.validation.constraints.*;
public record RecipeIngredientRequest(
    @NotNull Long ingredientId,
    @DecimalMin("0.01") Double quantity,
    @NotBlank @Pattern(regexp = "^(g|kg|pcs)$", message = "unit must be g, kg, or pcs") String unit) {}
