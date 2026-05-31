package com.recipes.dto;

public record ShoppingItemRequest(
    Long ingredientId,
    String name,
    Double quantity,
    String unit
) {}
