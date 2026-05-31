package com.recipes.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
public record RecipeStepRequest(@Min(1) int stepNumber, @NotBlank String description) {}
