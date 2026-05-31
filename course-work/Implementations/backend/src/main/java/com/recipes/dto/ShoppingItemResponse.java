package com.recipes.dto;

public record ShoppingItemResponse(
    Long id,
    Long ingredientId,
    String ingredientName,
    String name,
    Double quantity,
    String unit,
    boolean checked,
    String category
) {}
