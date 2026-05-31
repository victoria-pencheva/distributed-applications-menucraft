package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.*;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeIngredientService {
    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;
    private final RecipeIngredientRepository riRepo;

    public RecipeIngredientService(RecipeRepository recipeRepo, IngredientRepository ingredientRepo, RecipeIngredientRepository riRepo) {
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
        this.riRepo = riRepo;
    }

    @Transactional
    public RecipeIngredientResponse add(Long recipeId, RecipeIngredientRequest req, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        checkOwnerOrAdmin(recipe, username);
        Ingredient ingredient = ingredientRepo.findById(req.ingredientId())
            .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + req.ingredientId()));
        RecipeIngredient ri = riRepo.findByRecipeIdAndIngredientId(recipeId, req.ingredientId())
            .orElse(new RecipeIngredient());
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setQuantity(req.quantity());
        ri.setUnit(req.unit());
        RecipeIngredient saved = riRepo.save(ri);
        return new RecipeIngredientResponse(saved.getId(), ingredient.getId(),
            ingredient.getName(), saved.getQuantity(), saved.getUnit());
    }

    @Transactional
    public void remove(Long recipeId, Long ingredientId, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        checkOwnerOrAdmin(recipe, username);
        riRepo.deleteByRecipeIdAndIngredientId(recipeId, ingredientId);
    }

    private void checkOwnerOrAdmin(Recipe recipe, String username) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !recipe.getUser().getUsername().equals(username))
            throw new AccessDeniedException("Not the owner of this recipe");
    }
}
