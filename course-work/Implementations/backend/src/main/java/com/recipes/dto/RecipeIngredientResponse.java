package com.recipes.dto;
public record RecipeIngredientResponse(Long id, Long ingredientId, String ingredientName,
    Double quantity, String unit) {}
