package com.recipes.dto;

import java.time.LocalDate;

public record MealPlanEntryResponse(
    Long id,
    Long recipeId,
    String recipeName,
    String recipeImageUrl,
    String recipeCuisine,
    int recipePrepTime,
    int recipeCookTime,
    LocalDate date,
    String mealType,
    int servings,
    Double calories,
    Double protein,
    Double carbs,
    Double fat
) {}
