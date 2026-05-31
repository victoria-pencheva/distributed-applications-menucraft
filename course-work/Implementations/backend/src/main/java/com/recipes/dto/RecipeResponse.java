package com.recipes.dto;
import java.time.LocalDateTime;
import java.util.List;
public record RecipeResponse(
    Long id, String name, String description,
    int prepTime, int cookTime, int servings,
    String difficulty, String imageUrl, LocalDateTime createdAt,
    String username, Long categoryId, String categoryName,
    List<RecipeIngredientResponse> ingredients, List<RecipeStepResponse> steps,
    double totalCalories, double caloriesPerServing,
    String cuisine, String blurb, List<String> tags,
    Double protein, Double carbs, Double fat, Double fiber,
    Double avgRating, Long ratingCount, Integer myRating) {}
