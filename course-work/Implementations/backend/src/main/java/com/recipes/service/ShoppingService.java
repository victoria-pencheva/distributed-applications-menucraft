package com.recipes.service;

import com.recipes.dto.ShoppingItemRequest;
import com.recipes.dto.ShoppingItemResponse;
import com.recipes.entity.Ingredient;
import com.recipes.entity.MealPlanEntry;
import com.recipes.entity.PantryItem;
import com.recipes.entity.RecipeIngredient;
import com.recipes.entity.ShoppingItem;
import com.recipes.util.QuantityConverter;
import com.recipes.entity.User;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.IngredientRepository;
import com.recipes.repository.MealPlanEntryRepository;
import com.recipes.repository.PantryItemRepository;
import com.recipes.repository.ShoppingItemRepository;
import com.recipes.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ShoppingService {

    private final ShoppingItemRepository shoppingRepo;
    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepo;
    private final MealPlanEntryRepository mealPlanRepo;
    private final PantryItemRepository pantryRepo;

    public ShoppingService(ShoppingItemRepository shoppingRepo,
                           IngredientRepository ingredientRepo,
                           UserRepository userRepo,
                           MealPlanEntryRepository mealPlanRepo,
                           PantryItemRepository pantryRepo) {
        this.shoppingRepo = shoppingRepo;
        this.ingredientRepo = ingredientRepo;
        this.userRepo = userRepo;
        this.mealPlanRepo = mealPlanRepo;
        this.pantryRepo = pantryRepo;
    }

    public List<ShoppingItemResponse> getAll(String username) {
        return shoppingRepo.findByUserUsernameOrderByCheckedAscCreatedAtAsc(username)
                           .stream()
                           .map(this::toResponse)
                           .toList();
    }

    @Transactional
    public ShoppingItemResponse add(ShoppingItemRequest req, String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (req.ingredientId() != null) {
            Optional<ShoppingItem> existing = shoppingRepo
                .findFirstByUserUsernameAndIngredientIdAndCheckedFalse(username, req.ingredientId());
            if (existing.isPresent()) {
                ShoppingItem item = existing.get();
                double existQty = item.getQuantity() != null ? item.getQuantity() : 0;
                double newQty  = req.quantity()  != null ? req.quantity()  : 0;
                String existUnit = item.getUnit() != null ? item.getUnit() : "g";
                String newUnit   = req.unit()     != null ? req.unit()     : "g";

                boolean existIsPcs = "pcs".equals(existUnit);
                boolean newIsPcs   = "pcs".equals(newUnit);
                if (existIsPcs == newIsPcs) {
                    if (existIsPcs) {
                        item.setQuantity(existQty + newQty);
                    } else {
                        double totalG = QuantityConverter.toGrams(existQty, existUnit) + QuantityConverter.toGrams(newQty, newUnit);
                        item.setQuantity(QuantityConverter.fromGrams(totalG, existUnit));
                    }
                    return toResponse(shoppingRepo.save(item));
                }
            }
        }

        ShoppingItem item = new ShoppingItem();
        item.setUser(user);

        if (req.ingredientId() != null) {
            Ingredient ingredient = ingredientRepo.findById(req.ingredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found"));
            item.setIngredient(ingredient);
        }

        item.setName(req.name());
        item.setQuantity(req.quantity());
        item.setUnit(req.unit());

        return toResponse(shoppingRepo.save(item));
    }

    @Transactional
    public void delete(Long itemId, String username) {
        ShoppingItem item = shoppingRepo.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Shopping item not found"));
        if (!item.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your shopping item");
        }
        shoppingRepo.delete(item);
    }

    @Transactional
    public void toggle(Long itemId, String username) {
        ShoppingItem item = shoppingRepo.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Shopping item not found"));
        if (!item.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your shopping item");
        }
        item.setChecked(!item.isChecked());
        shoppingRepo.save(item);
    }

    @Transactional
    public void clearChecked(String username) {
        shoppingRepo.deleteCheckedByUserUsername(username);
    }

    @Transactional
    public void clearAll(String username) {
        shoppingRepo.deleteByUserUsername(username);
    }

    @Transactional
    public List<ShoppingItemResponse> generateFromMealPlan(String username, String weekStart) {
        LocalDate start = weekStart.matches("\\d{4}-W\\d{1,2}")
            ? LocalDate.parse(weekStart + "-1", DateTimeFormatter.ISO_WEEK_DATE)
            : LocalDate.parse(weekStart);
        LocalDate end = start.plusDays(6);

        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<MealPlanEntry> entries = mealPlanRepo.findByUserUsernameAndDateBetween(username, start, end);

        Set<Long> pantryIngredientIds = new HashSet<>();
        for (PantryItem p : pantryRepo.findByUserUsername(username)) {
            pantryIngredientIds.add(p.getIngredient().getId());
        }

        shoppingRepo.deleteUncheckedByUserUsername(username);

        Map<Long, double[]> qtyMap = new LinkedHashMap<>();
        Map<Long, Ingredient> ingMap = new LinkedHashMap<>();

        for (MealPlanEntry entry : entries) {
            for (RecipeIngredient ri : entry.getRecipe().getRecipeIngredients()) {
                Long ingId = ri.getIngredient().getId();
                if (pantryIngredientIds.contains(ingId)) continue;

                double raw = ri.getQuantity() != null ? ri.getQuantity() : 0;
                boolean isPcs = "pcs".equals(ri.getUnit());
                double normalized = QuantityConverter.toGrams(raw, ri.getUnit());

                if (!qtyMap.containsKey(ingId)) {
                    qtyMap.put(ingId, new double[]{normalized, isPcs ? 1 : 0});
                    ingMap.put(ingId, ri.getIngredient());
                } else {
                    qtyMap.get(ingId)[0] += normalized;
                    if (isPcs) qtyMap.get(ingId)[1] = 1;
                }
            }
        }

        List<ShoppingItem> created = new ArrayList<>();
        for (Map.Entry<Long, double[]> e : qtyMap.entrySet()) {
            double total   = e.getValue()[0];
            boolean isPcs  = e.getValue()[1] > 0;

            double displayQty;
            String displayUnit;
            if (isPcs) {
                displayQty  = Math.round(total);
                displayUnit = "pcs";
            } else if (total >= 1000) {
                displayQty  = Math.round(total / 10.0) / 100.0;
                displayUnit = "kg";
            } else {
                displayQty  = Math.round(total);
                displayUnit = "g";
            }

            ShoppingItem item = new ShoppingItem();
            item.setUser(user);
            item.setIngredient(ingMap.get(e.getKey()));
            item.setQuantity(displayQty);
            item.setUnit(displayUnit);
            created.add(shoppingRepo.save(item));
        }

        return created.stream().map(this::toResponse).toList();
    }

    private ShoppingItemResponse toResponse(ShoppingItem item) {
        Long ingredientId = item.getIngredient() != null ? item.getIngredient().getId() : null;
        String ingredientName = item.getIngredient() != null ? item.getIngredient().getName() : null;
        String category = item.getIngredient() != null && item.getIngredient().getCategory() != null
            ? item.getIngredient().getCategory().name() : null;
        return new ShoppingItemResponse(
            item.getId(),
            ingredientId,
            ingredientName,
            item.getName(),
            item.getQuantity(),
            item.getUnit(),
            item.isChecked(),
            category
        );
    }
}
