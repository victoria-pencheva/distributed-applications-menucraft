package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.service.RecipeIngredientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
@Tag(name = "Recipe Ingredients")
public class RecipeIngredientController {
    private final RecipeIngredientService service;

    public RecipeIngredientController(RecipeIngredientService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeIngredientResponse add(@PathVariable Long recipeId,
                                        @Valid @RequestBody RecipeIngredientRequest req,
                                        Authentication auth) {
        return service.add(recipeId, req, auth.getName());
    }

    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long recipeId, @PathVariable Long ingredientId, Authentication auth) {
        service.remove(recipeId, ingredientId, auth.getName());
    }
}
