package com.recipes.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MealPlanEntryRequest(
    @NotNull Long recipeId,
    @NotNull LocalDate date,
    @NotNull String mealType,
    Integer servings
) {}
