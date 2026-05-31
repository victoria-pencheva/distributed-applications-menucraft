package com.recipes.controller;

import com.recipes.dto.RecipeStepRequest;
import com.recipes.dto.RecipeStepResponse;
import com.recipes.service.RecipeStepService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/steps")
@Tag(name = "Recipe Steps")
public class RecipeStepController {
    private final RecipeStepService service;

    public RecipeStepController(RecipeStepService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeStepResponse add(@PathVariable Long recipeId,
                                   @Valid @RequestBody RecipeStepRequest req,
                                   Authentication auth) {
        return service.add(recipeId, req, auth.getName());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll(@PathVariable Long recipeId, Authentication auth) {
        service.removeAll(recipeId, auth.getName());
    }

    @DeleteMapping("/{stepId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long recipeId, @PathVariable Long stepId, Authentication auth) {
        service.remove(recipeId, stepId, auth.getName());
    }
}
