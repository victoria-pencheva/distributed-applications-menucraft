package com.recipes.repository;
import com.recipes.entity.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"recipeIngredients", "recipeIngredients.ingredient", "steps", "category"})
    Optional<Recipe> findById(Long id);
}
