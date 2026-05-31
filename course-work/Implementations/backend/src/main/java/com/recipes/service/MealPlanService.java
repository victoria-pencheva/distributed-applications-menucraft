package com.recipes.service;

import com.recipes.dto.MealPlanEntryRequest;
import com.recipes.dto.MealPlanEntryResponse;
import com.recipes.entity.MealPlanEntry;
import com.recipes.entity.MealType;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.MealPlanEntryRepository;
import com.recipes.repository.RecipeRepository;
import com.recipes.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealPlanService {

    private final MealPlanEntryRepository repo;
    private final RecipeRepository recipeRepo;
    private final UserRepository userRepo;

    public MealPlanService(MealPlanEntryRepository repo,
                           RecipeRepository recipeRepo,
                           UserRepository userRepo) {
        this.repo = repo;
        this.recipeRepo = recipeRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<MealPlanEntryResponse> getWeek(String username, LocalDate weekStart) {
        return repo.findByUserUsernameAndDateBetween(username, weekStart, weekStart.plusDays(6))
                   .stream()
                   .map(this::toResponse)
                   .toList();
    }

    @Transactional
    public MealPlanEntryResponse add(MealPlanEntryRequest req, String username) {
        MealType mt = MealType.valueOf(req.mealType().toUpperCase());

        repo.findByUserUsernameAndDateAndMealType(username, req.date(), mt)
            .ifPresent(repo::delete);

        MealPlanEntry entry = new MealPlanEntry();
        entry.setUser(userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        entry.setRecipe(recipeRepo.findById(req.recipeId())
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found")));
        entry.setDate(req.date());
        entry.setMealType(mt);
        entry.setServings(req.servings() != null ? Math.max(1, req.servings()) : 1);

        return toResponse(repo.save(entry));
    }

    @Transactional
    public void delete(Long entryId, String username) {
        MealPlanEntry entry = repo.findById(entryId)
            .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        if (!entry.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your entry");
        }
        repo.delete(entry);
    }

    @Transactional
    public MealPlanEntryResponse updateServings(Long entryId, int servings, String username) {
        MealPlanEntry entry = repo.findById(entryId)
            .orElseThrow(() -> new com.recipes.exception.ResourceNotFoundException("Entry not found"));
        if (!entry.getUser().getUsername().equals(username))
            throw new org.springframework.security.access.AccessDeniedException("Not your entry");
        entry.setServings(Math.max(1, servings));
        return toResponse(repo.save(entry));
    }

    private MealPlanEntryResponse toResponse(MealPlanEntry e) {
        var recipe = e.getRecipe();
        Double calories = null;
        if (recipe.getProtein() != null && recipe.getCarbs() != null && recipe.getFat() != null) {
            calories = (recipe.getProtein() * 4 + recipe.getCarbs() * 4 + recipe.getFat() * 9) * e.getServings();
        }
        Double protein = recipe.getProtein() != null ? recipe.getProtein() * e.getServings() : null;
        Double carbs   = recipe.getCarbs()   != null ? recipe.getCarbs()   * e.getServings() : null;
        Double fat     = recipe.getFat()     != null ? recipe.getFat()     * e.getServings() : null;

        return new MealPlanEntryResponse(
            e.getId(),
            recipe.getId(),
            recipe.getName(),
            recipe.getImageUrl(),
            recipe.getCuisine(),
            recipe.getPrepTime(),
            recipe.getCookTime(),
            e.getDate(),
            e.getMealType().name(),
            e.getServings(),
            calories,
            protein,
            carbs,
            fat
        );
    }
}
