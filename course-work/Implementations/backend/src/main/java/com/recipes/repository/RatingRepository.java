package com.recipes.repository;

import com.recipes.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByRecipeIdAndUserUsername(Long recipeId, String username);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double avgByRecipeId(Long recipeId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.recipe.id = :recipeId")
    Long countByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);
}
