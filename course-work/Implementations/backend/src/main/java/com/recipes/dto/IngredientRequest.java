package com.recipes.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record IngredientRequest(
    @NotBlank String name,
    String description,
    @NotBlank @Pattern(regexp = "^(g|kg|pcs)$", message = "Unit must be g, kg or pcs") String defaultUnit,
    Double calories,
    Double protein,
    Double carbs,
    Double fat,
    Double fiber,
    String category,
    boolean containsGluten) {}
