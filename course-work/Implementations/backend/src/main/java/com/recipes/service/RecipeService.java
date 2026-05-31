package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.*;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.*;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecipeService {
    private final RecipeRepository recipeRepo;
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;
    private final MealPlanEntryRepository mealPlanEntryRepo;
    private final RecipeIngredientRepository riRepo;
    private final IngredientRepository ingredientRepo;
    private final RecipeStepRepository stepRepo;

    public RecipeService(RecipeRepository recipeRepo, CategoryRepository categoryRepo,
                         UserRepository userRepo, RatingRepository ratingRepo,
                         MealPlanEntryRepository mealPlanEntryRepo,
                         RecipeIngredientRepository riRepo,
                         IngredientRepository ingredientRepo,
                         RecipeStepRepository stepRepo) {
        this.recipeRepo = recipeRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
        this.mealPlanEntryRepo = mealPlanEntryRepo;
        this.riRepo = riRepo;
        this.ingredientRepo = ingredientRepo;
        this.stepRepo = stepRepo;
    }

    public Page<RecipeResponse> findAll(Pageable pageable) {
        return recipeRepo.findAll(pageable).map(this::toResponse);
    }

    public RecipeResponse findById(Long id) {
        return toResponse(recipeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id)));
    }

    @Transactional
    public RecipeResponse create(RecipeRequest req, String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Recipe recipe = new Recipe();
        applyRequest(recipe, req);
        recipe.setUser(user);
        return toResponse(recipeRepo.save(recipe));
    }

    @Transactional
    public RecipeResponse updateFull(Long id, RecipeFullUpdateRequest req, String username) {
        Recipe recipe = recipeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id));
        checkOwnerOrAdmin(recipe, username);

        applyRequest(recipe, new RecipeRequest(req.name(), req.description(), req.prepTime(),
            req.cookTime(), req.servings(), req.difficulty(), req.imageUrl(), req.categoryId(),
            req.cuisine(), req.blurb(), req.protein(), req.carbs(), req.fat(), req.fiber()));
        recipeRepo.save(recipe);

        riRepo.deleteByRecipeId(id);
        if (req.ingredients() != null) {
            for (RecipeIngredientRequest ir : req.ingredients()) {
                Ingredient ing = ingredientRepo.findById(ir.ingredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + ir.ingredientId()));
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(recipe);
                ri.setIngredient(ing);
                ri.setQuantity(ir.quantity());
                ri.setUnit(ir.unit());
                riRepo.save(ri);
            }
        }

        stepRepo.deleteByRecipeId(id);
        if (req.steps() != null) {
            for (RecipeStepRequest sr : req.steps()) {
                RecipeStep step = new RecipeStep();
                step.setRecipe(recipe);
                step.setStepNumber(sr.stepNumber());
                step.setDescription(sr.description());
                stepRepo.save(step);
            }
        }

        return toResponse(recipeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id)));
    }

    @Transactional
    public RecipeResponse update(Long id, RecipeRequest req, String username) {
        Recipe recipe = recipeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id));
        checkOwnerOrAdmin(recipe, username);
        applyRequest(recipe, req);
        return toResponse(recipeRepo.save(recipe));
    }

    @Transactional
    public void delete(Long id, String username, boolean force) {
        Recipe recipe = recipeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id));
        checkOwnerOrAdmin(recipe, username);
        if (!force && mealPlanEntryRepo.existsByRecipeId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Recipe is used in active meal plans. Delete with force=true to remove it anyway.");
        }
        mealPlanEntryRepo.deleteByRecipeId(id);
        ratingRepo.deleteByRecipeId(id);
        recipeRepo.delete(recipe);
    }

    public Page<RecipeResponse> search(String name, Long categoryId, Long ingredientId,
                                       String difficulty, Integer minPrepTime, Integer maxPrepTime,
                                       List<Long> includeIngredientIds, List<Long> excludeIngredientIds,
                                       String cuisine, List<String> tags,
                                       Double maxCalories, Double minProtein, Double maxCarbs, Double maxFat,
                                       Pageable pageable) {
        Specification<Recipe> spec = Specification.where(null);

        if (name != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("name")), "%" + name.toLowerCase() + "%"));
        if (categoryId != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("category").get("id"), categoryId));
        if (ingredientId != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.join("recipeIngredients").get("ingredient").get("id"), ingredientId));
        if (difficulty != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("difficulty"), Difficulty.valueOf(difficulty.toUpperCase())));
        if (minPrepTime != null)
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("prepTime"), minPrepTime));
        if (maxPrepTime != null)
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("prepTime"), maxPrepTime));
        if (cuisine != null)
            spec = spec.and((r, q, cb) -> cb.equal(cb.lower(r.get("cuisine")), cuisine.toLowerCase()));

        if (includeIngredientIds != null) {
            for (Long ingId : includeIngredientIds) {
                final Long fId = ingId;
                spec = spec.and((r, q, cb) -> {
                    Subquery<Long> sub = q.subquery(Long.class);
                    Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                    sub.select(ri.get("recipe").get("id"))
                       .where(cb.equal(ri.get("ingredient").get("id"), fId));
                    return cb.in(r.get("id")).value(sub);
                });
            }
        }
        if (excludeIngredientIds != null && !excludeIngredientIds.isEmpty()) {
            spec = spec.and((r, q, cb) -> {
                Subquery<Long> sub = q.subquery(Long.class);
                Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                sub.select(ri.get("recipe").get("id"))
                   .where(ri.get("ingredient").get("id").in(excludeIngredientIds));
                return cb.not(cb.in(r.get("id")).value(sub));
            });
        }

        if (tags != null) {
            for (String tag : tags) {
                spec = spec.and(tagSpec(tag));
            }
        }
        if (maxCalories != null)
            spec = spec.and((r, q, cb) -> cb.and(
                cb.isNotNull(r.get("computedCaloriesPerServing")),
                cb.lessThanOrEqualTo(r.get("computedCaloriesPerServing"), maxCalories)));
        if (minProtein != null)
            spec = spec.and((r, q, cb) -> cb.and(
                cb.isNotNull(r.get("protein")),
                cb.greaterThanOrEqualTo(r.get("protein"), minProtein)));
        if (maxCarbs != null)
            spec = spec.and((r, q, cb) -> cb.and(
                cb.isNotNull(r.get("carbs")),
                cb.lessThanOrEqualTo(r.get("carbs"), maxCarbs)));
        if (maxFat != null)
            spec = spec.and((r, q, cb) -> cb.and(
                cb.isNotNull(r.get("fat")),
                cb.lessThanOrEqualTo(r.get("fat"), maxFat)));

        return recipeRepo.findAll(spec, pageable).map(this::toResponse);
    }

    private Specification<Recipe> tagSpec(String tag) {
        return switch (tag) {
            case "Vegetarian" -> (r, q, cb) -> {
                Subquery<Long> sub = q.subquery(Long.class);
                Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                sub.select(ri.get("recipe").get("id"))
                   .where(ri.get("ingredient").get("category").in(
                       IngredientCategory.MEAT, IngredientCategory.POULTRY, IngredientCategory.SEAFOOD));
                return cb.not(cb.in(r.get("id")).value(sub));
            };
            case "Vegan" -> (r, q, cb) -> {
                Subquery<Long> sub = q.subquery(Long.class);
                Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                sub.select(ri.get("recipe").get("id"))
                   .where(ri.get("ingredient").get("category").in(
                       IngredientCategory.MEAT, IngredientCategory.POULTRY, IngredientCategory.SEAFOOD,
                       IngredientCategory.DAIRY, IngredientCategory.EGGS));
                return cb.not(cb.in(r.get("id")).value(sub));
            };
            case "Dairy-Free" -> (r, q, cb) -> {
                Subquery<Long> sub = q.subquery(Long.class);
                Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                sub.select(ri.get("recipe").get("id"))
                   .where(cb.equal(ri.get("ingredient").get("category"), IngredientCategory.DAIRY));
                return cb.not(cb.in(r.get("id")).value(sub));
            };
            case "Gluten-Free" -> (r, q, cb) -> {
                Subquery<Long> sub = q.subquery(Long.class);
                Root<RecipeIngredient> ri = sub.from(RecipeIngredient.class);
                sub.select(ri.get("recipe").get("id"))
                   .where(cb.isTrue(ri.get("ingredient").get("containsGluten")));
                return cb.not(cb.in(r.get("id")).value(sub));
            };
            case "High-Protein" -> (r, q, cb) ->
                cb.and(cb.isNotNull(r.get("protein")), cb.greaterThanOrEqualTo(r.get("protein"), 25.0));
            case "Low-Carb" -> (r, q, cb) ->
                cb.and(cb.isNotNull(r.get("carbs")), cb.lessThanOrEqualTo(r.get("carbs"), 20.0));
            case "Low-Calorie" -> (r, q, cb) ->
                cb.and(
                    cb.isNotNull(r.get("protein")), cb.isNotNull(r.get("carbs")), cb.isNotNull(r.get("fat")),
                    cb.lessThanOrEqualTo(
                        cb.sum(cb.sum(
                            cb.prod(r.<Double>get("protein"), 4.0),
                            cb.prod(r.<Double>get("carbs"), 4.0)),
                            cb.prod(r.<Double>get("fat"), 9.0)),
                        400.0));
            default -> Specification.where(null);
        };
    }

    private void applyRequest(Recipe recipe, RecipeRequest req) {
        recipe.setName(req.name());
        recipe.setDescription(req.description());
        recipe.setPrepTime(req.prepTime());
        recipe.setCookTime(req.cookTime());
        recipe.setServings(req.servings());
        recipe.setDifficulty(req.difficulty());
        recipe.setImageUrl(req.imageUrl());
        if (req.cuisine() != null) recipe.setCuisine(req.cuisine());
        if (req.blurb() != null)   recipe.setBlurb(req.blurb());
        if (req.protein() != null) recipe.setProtein(req.protein());
        if (req.carbs() != null)   recipe.setCarbs(req.carbs());
        if (req.fat() != null)     recipe.setFat(req.fat());
        if (req.fiber() != null)   recipe.setFiber(req.fiber());
        if (req.categoryId() != null) {
            recipe.setCategory(categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.categoryId())));
        } else {
            recipe.setCategory(null);
        }
    }

    private void checkOwnerOrAdmin(Recipe recipe, String username) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !recipe.getUser().getUsername().equals(username))
            throw new AccessDeniedException("Not the owner of this recipe");
    }

    RecipeResponse toResponse(Recipe r) {
        List<RecipeIngredientResponse> ris = r.getRecipeIngredients().stream()
            .map(ri -> new RecipeIngredientResponse(ri.getId(), ri.getIngredient().getId(),
                ri.getIngredient().getName(), ri.getQuantity(), ri.getUnit()))
            .toList();
        List<RecipeStepResponse> steps = r.getSteps().stream()
            .map(s -> new RecipeStepResponse(s.getId(), s.getStepNumber(), s.getDescription()))
            .toList();
        double totalCalories = r.getRecipeIngredients().stream()
            .mapToDouble(ri -> {
                Double cal = ri.getIngredient().getCalories();
                if (cal == null || cal <= 0) return 0.0;
                String unit = ri.getUnit();
                if ("kg".equals(unit))  return cal * ri.getQuantity() * 10.0;
                if ("pcs".equals(unit)) return cal * ri.getQuantity();
                return cal * ri.getQuantity() / 100.0;
            }).sum();
        double caloriesPerServing = r.getServings() > 0 ? totalCalories / r.getServings() : totalCalories;

        boolean hasMeat = r.getRecipeIngredients().stream().anyMatch(ri -> {
            IngredientCategory c = ri.getIngredient().getCategory();
            return c == IngredientCategory.MEAT || c == IngredientCategory.POULTRY || c == IngredientCategory.SEAFOOD;
        });
        boolean hasDairy = r.getRecipeIngredients().stream()
            .anyMatch(ri -> ri.getIngredient().getCategory() == IngredientCategory.DAIRY);
        boolean hasEggs = r.getRecipeIngredients().stream()
            .anyMatch(ri -> ri.getIngredient().getCategory() == IngredientCategory.EGGS);
        boolean hasGluten = r.getRecipeIngredients().stream()
            .anyMatch(ri -> ri.getIngredient().isContainsGluten());

        List<String> tags = new ArrayList<>();
        if (!hasMeat && !hasDairy && !hasEggs) tags.add("Vegan");
        else if (!hasMeat)                     tags.add("Vegetarian");
        if (!hasDairy)  tags.add("Dairy-Free");
        if (!hasGluten) tags.add("Gluten-Free");
        if (r.getProtein() != null && r.getProtein() >= 25) tags.add("High-Protein");
        if (r.getCarbs()   != null && r.getCarbs()   <= 20) tags.add("Low-Carb");
        if (caloriesPerServing > 0 && caloriesPerServing <= 400) tags.add("Low-Calorie");

        String currentUser = null;
        try {
            currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {}

        Double avgRating = ratingRepo.avgByRecipeId(r.getId());
        Long ratingCount = ratingRepo.countByRecipeId(r.getId());
        Integer myRating = null;
        if (currentUser != null) {
            myRating = ratingRepo.findByRecipeIdAndUserUsername(r.getId(), currentUser)
                .map(rt -> rt.getStars()).orElse(null);
        }

        return new RecipeResponse(r.getId(), r.getName(), r.getDescription(),
            r.getPrepTime(), r.getCookTime(), r.getServings(), r.getDifficulty().name(),
            r.getImageUrl(), r.getCreatedAt(), r.getUser().getUsername(),
            r.getCategory() != null ? r.getCategory().getId() : null,
            r.getCategory() != null ? r.getCategory().getName() : null,
            ris, steps,
            Math.round(totalCalories * 10.0) / 10.0,
            Math.round(caloriesPerServing * 10.0) / 10.0,
            r.getCuisine(), r.getBlurb(), tags,
            r.getProtein(), r.getCarbs(), r.getFat(), r.getFiber(),
            avgRating, ratingCount, myRating);
    }
}
