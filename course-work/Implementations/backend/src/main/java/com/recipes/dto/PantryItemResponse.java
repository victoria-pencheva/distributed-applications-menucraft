package com.recipes.dto;

public record PantryItemResponse(
    Long id,
    Long ingredientId,
    String ingredientName,
    double quantity,
    String unit
) {}
