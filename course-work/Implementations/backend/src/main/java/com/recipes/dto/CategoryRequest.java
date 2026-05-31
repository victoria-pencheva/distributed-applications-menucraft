package com.recipes.dto;
import jakarta.validation.constraints.*;
public record CategoryRequest(
    @NotBlank @Size(max=50) String name,
    String description,
    String imageUrl) {}
