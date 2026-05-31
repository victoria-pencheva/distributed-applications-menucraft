package com.recipes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PantryItemRequest(
    @NotNull Long ingredientId,
    @DecimalMin("0") Double quantity,
    @NotBlank String unit
) {}
