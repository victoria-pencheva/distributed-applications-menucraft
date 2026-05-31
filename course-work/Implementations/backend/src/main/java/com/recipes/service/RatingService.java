package com.recipes.service;

import com.recipes.dto.RatingRequest;
import com.recipes.entity.Rating;
import com.recipes.entity.Recipe;
import com.recipes.entity.User;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.RatingRepository;
import com.recipes.repository.RecipeRepository;
import com.recipes.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepo;
    private final RecipeRepository recipeRepo;
    private final UserRepository userRepo;

    public RatingService(RatingRepository ratingRepo, RecipeRepository recipeRepo, UserRepository userRepo) {
        this.ratingRepo = ratingRepo;
        this.recipeRepo = recipeRepo;
        this.userRepo = userRepo;
    }

    public void upsert(Long recipeId, RatingRequest req, String username) {
        Recipe recipe = recipeRepo.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Rating rating = ratingRepo.findByRecipeIdAndUserUsername(recipeId, username)
            .orElseGet(() -> {
                Rating r = new Rating();
                r.setRecipe(recipe);
                r.setUser(user);
                return r;
            });
        rating.setStars(req.stars());
        ratingRepo.save(rating);
    }

    public void delete(Long recipeId, String username) {
        ratingRepo.findByRecipeIdAndUserUsername(recipeId, username)
            .ifPresent(ratingRepo::delete);
    }
}
