package com.recipes.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(
    @NotBlank @Size(min=3,max=50) String username,
    @NotBlank @Email String email,
    @NotBlank @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Password must include uppercase, lowercase, number, and special character"
    ) String password) {}
