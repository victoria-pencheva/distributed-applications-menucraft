package com.recipes.service;

import com.recipes.dto.RecipeStepRequest;
import com.recipes.dto.RecipeStepResponse;
import com.recipes.entity.Recipe;
import com.recipes.entity.RecipeStep;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.RecipeRepository;
import com.recipes.repository.RecipeStepRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeStepService {
    private final RecipeRepository recipeRepo;
    private final RecipeStepRepository stepRepo;

    public RecipeStepService(RecipeRepository recipeRepo, RecipeStepRepository stepRepo) {
        this.recipeRepo = recipeRepo;
        this.stepRepo = stepRepo;
    }

    public RecipeStepResponse add(Long recipeId, RecipeStepRequest req, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        checkOwnerOrAdmin(recipe, username);
        RecipeStep step = new RecipeStep();
        step.setRecipe(recipe);
        step.setStepNumber(req.stepNumber());
        step.setDescription(req.description());
        RecipeStep saved = stepRepo.save(step);
        return new RecipeStepResponse(saved.getId(), saved.getStepNumber(), saved.getDescription());
    }

    @Transactional
    public void removeAll(Long recipeId, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        checkOwnerOrAdmin(recipe, username);
        stepRepo.deleteByRecipeId(recipeId);
    }

    @Transactional
    public void remove(Long recipeId, Long stepId, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        checkOwnerOrAdmin(recipe, username);
        stepRepo.findById(stepId).ifPresent(step -> {
            if (step.getRecipe().getId().equals(recipeId)) stepRepo.delete(step);
        });
    }

    private void checkOwnerOrAdmin(Recipe recipe, String username) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !recipe.getUser().getUsername().equals(username))
            throw new AccessDeniedException("Not the owner of this recipe");
    }
}
