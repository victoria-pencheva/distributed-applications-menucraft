package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.Ingredient;
import com.recipes.entity.IngredientCategory;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.IngredientRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
    private final IngredientRepository repo;

    public IngredientService(IngredientRepository repo) {
        this.repo = repo;
    }

    public Page<IngredientResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    public IngredientResponse findById(Long id) {
        return toResponse(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + id)));
    }

    public IngredientResponse create(IngredientRequest req) {
        Ingredient i = new Ingredient();
        applyRequest(i, req);
        return toResponse(repo.save(i));
    }

    public IngredientResponse update(Long id, IngredientRequest req) {
        Ingredient i = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + id));
        applyRequest(i, req);
        return toResponse(repo.save(i));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Ingredient not found: " + id);
        repo.deleteById(id);
    }

    private void applyRequest(Ingredient i, IngredientRequest req) {
        i.setName(req.name());
        i.setDescription(req.description());
        i.setDefaultUnit(req.defaultUnit());
        i.setCalories(req.calories());
        i.setProtein(req.protein());
        i.setCarbs(req.carbs());
        i.setFat(req.fat());
        i.setFiber(req.fiber());
        i.setContainsGluten(req.containsGluten());
        if (req.category() != null && !req.category().isBlank()) {
            try {
                i.setCategory(IngredientCategory.valueOf(req.category().toUpperCase()));
            } catch (IllegalArgumentException e) {
                i.setCategory(null);
            }
        } else {
            i.setCategory(null);
        }
    }

    private IngredientResponse toResponse(Ingredient i) {
        return new IngredientResponse(i.getId(), i.getName(), i.getDescription(),
            i.getDefaultUnit(), i.getCalories(), i.getProtein(), i.getCarbs(), i.getFat(), i.getFiber(),
            i.getCreatedAt(), i.getUpdatedAt(),
            i.getCategory() != null ? i.getCategory().name() : null,
            i.isContainsGluten());
    }
}
