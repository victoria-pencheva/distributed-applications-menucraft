package com.recipes.service;

import com.recipes.dto.PantryItemRequest;
import com.recipes.dto.PantryItemResponse;
import com.recipes.entity.Ingredient;
import com.recipes.entity.PantryItem;
import com.recipes.entity.User;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.IngredientRepository;
import com.recipes.repository.PantryItemRepository;
import com.recipes.repository.UserRepository;
import com.recipes.util.QuantityConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PantryService {

    private final PantryItemRepository pantryRepo;
    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepo;

    public PantryService(PantryItemRepository pantryRepo,
                         IngredientRepository ingredientRepo,
                         UserRepository userRepo) {
        this.pantryRepo = pantryRepo;
        this.ingredientRepo = ingredientRepo;
        this.userRepo = userRepo;
    }

    public List<PantryItemResponse> getAll(String username) {
        return pantryRepo.findByUserUsername(username)
                         .stream()
                         .map(this::toResponse)
                         .toList();
    }

    @Transactional
    public PantryItemResponse upsert(PantryItemRequest req, String username) {
        PantryItem item = pantryRepo
            .findByUserUsernameAndIngredientId(username, req.ingredientId())
            .orElseGet(() -> {
                User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                Ingredient ingredient = ingredientRepo.findById(req.ingredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found"));
                PantryItem newItem = new PantryItem();
                newItem.setUser(user);
                newItem.setIngredient(ingredient);
                return newItem;
            });

        item.setQuantity(req.quantity());
        item.setUnit(req.unit());

        return toResponse(pantryRepo.save(item));
    }

    @Transactional
    public PantryItemResponse increment(Long itemId, PantryItemRequest req, String username) {
        PantryItem item = pantryRepo.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Pantry item not found"));
        if (!item.getUser().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Not your pantry item");
        }

        double existQty  = item.getQuantity() != null ? item.getQuantity() : 0;
        double addQty    = req.quantity()     != null ? req.quantity()     : 0;
        String existUnit = item.getUnit() != null ? item.getUnit() : "g";
        String addUnit   = req.unit()     != null ? req.unit()     : "g";

        boolean existPcs = "pcs".equals(existUnit);
        boolean addPcs   = "pcs".equals(addUnit);

        if (existPcs == addPcs) {
            if (existPcs) {
                item.setQuantity(existQty + addQty);
            } else {
                double totalG = QuantityConverter.toGrams(existQty, existUnit) + QuantityConverter.toGrams(addQty, addUnit);
                item.setQuantity(QuantityConverter.fromGrams(totalG, existUnit));
            }
        }

        return toResponse(pantryRepo.save(item));
    }

    @Transactional
    public void clearAll(String username) {
        pantryRepo.deleteByUserUsername(username);
    }

    @Transactional
    public void delete(Long itemId, String username) {
        PantryItem item = pantryRepo.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Pantry item not found"));
        if (!item.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your pantry item");
        }
        pantryRepo.delete(item);
    }

    private PantryItemResponse toResponse(PantryItem item) {
        return new PantryItemResponse(
            item.getId(),
            item.getIngredient().getId(),
            item.getIngredient().getName(),
            item.getQuantity(),
            item.getUnit()
        );
    }
}
