package com.recipes.repository;

import com.recipes.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    @Modifying
    @Query("DELETE FROM RecipeStep s WHERE s.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
