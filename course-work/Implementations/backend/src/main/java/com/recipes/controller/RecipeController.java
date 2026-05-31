package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipes")
public class RecipeController {
    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping
    public Page<RecipeResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public RecipeResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse create(@Valid @RequestBody RecipeRequest req, Authentication auth) {
        return service.create(req, auth.getName());
    }

    @PutMapping("/{id}")
    public RecipeResponse update(@PathVariable Long id, @Valid @RequestBody RecipeRequest req, Authentication auth) {
        return service.update(id, req, auth.getName());
    }

    @PutMapping("/{id}/full")
    public RecipeResponse updateFull(@PathVariable Long id, @Valid @RequestBody RecipeFullUpdateRequest req, Authentication auth) {
        return service.updateFull(id, req, auth.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force,
                       Authentication auth) {
        service.delete(id, auth.getName(), force);
    }

    @GetMapping("/search")
    public Page<RecipeResponse> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long ingredientId,
        @RequestParam(required = false) String difficulty,
        @RequestParam(required = false) Integer minPrepTime,
        @RequestParam(required = false) Integer maxPrepTime,
        @RequestParam(required = false) List<Long> includeIngredientIds,
        @RequestParam(required = false) List<Long> excludeIngredientIds,
        @RequestParam(required = false) String cuisine,
        @RequestParam(required = false) List<String> tag,
        @RequestParam(required = false) Double maxCalories,
        @RequestParam(required = false) Double minProtein,
        @RequestParam(required = false) Double maxCarbs,
        @RequestParam(required = false) Double maxFat,
        @RequestParam(required = false, defaultValue = "new") String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "24") int size) {

        Sort springSort = switch (sort) {
            case "quick" -> Sort.by(Sort.Order.asc("totalTime"));
            case "cal"   -> Sort.by(Sort.Order.asc("computedCaloriesPerServing"));
            default      -> Sort.by(Sort.Order.desc("createdAt"));
        };
        Pageable pageable = PageRequest.of(page, size, springSort);

        return service.search(name, categoryId, ingredientId, difficulty, minPrepTime, maxPrepTime,
                includeIngredientIds, excludeIngredientIds, cuisine, tag,
                maxCalories, minProtein, maxCarbs, maxFat, pageable);
    }
}
