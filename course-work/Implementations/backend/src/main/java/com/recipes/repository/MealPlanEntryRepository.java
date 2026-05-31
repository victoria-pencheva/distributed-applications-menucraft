package com.recipes.repository;

import com.recipes.entity.MealPlanEntry;
import com.recipes.entity.MealType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanEntryRepository extends JpaRepository<MealPlanEntry, Long> {

    @EntityGraph(attributePaths = {"recipe"})
    List<MealPlanEntry> findByUserUsernameAndDateBetween(String username, LocalDate from, LocalDate to);

    Optional<MealPlanEntry> findByUserUsernameAndDateAndMealType(String username, LocalDate date, MealType mealType);

    boolean existsByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
}
