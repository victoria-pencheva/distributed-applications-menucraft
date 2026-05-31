package com.recipes.dto;
import java.time.LocalDateTime;
public record IngredientResponse(Long id, String name, String description, String defaultUnit,
    Double calories, Double protein, Double carbs, Double fat, Double fiber,
    LocalDateTime createdAt, LocalDateTime updatedAt,
    String category, boolean containsGluten) {}
