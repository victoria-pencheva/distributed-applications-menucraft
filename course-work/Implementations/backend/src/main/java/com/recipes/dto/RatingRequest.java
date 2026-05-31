package com.recipes.dto;

import jakarta.validation.constraints.*;

public record RatingRequest(@Min(1) @Max(5) int stars) {}
