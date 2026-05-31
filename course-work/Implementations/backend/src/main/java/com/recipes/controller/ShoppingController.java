package com.recipes.controller;

import com.recipes.dto.ShoppingItemRequest;
import com.recipes.dto.ShoppingItemResponse;
import com.recipes.service.ShoppingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    private final ShoppingService service;

    public ShoppingController(ShoppingService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShoppingItemResponse> getAll(@AuthenticationPrincipal UserDetails user) {
        return service.getAll(user.getUsername());
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ShoppingItemResponse> generateFromMealPlan(
            @RequestParam String weekStart,
            @AuthenticationPrincipal UserDetails user) {
        return service.generateFromMealPlan(user.getUsername(), weekStart);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemResponse add(
            @Valid @RequestBody ShoppingItemRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return service.add(req, user.getUsername());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll(@AuthenticationPrincipal UserDetails user) {
        service.clearAll(user.getUsername());
    }

    @DeleteMapping("/checked")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearChecked(@AuthenticationPrincipal UserDetails user) {
        service.clearChecked(user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        service.delete(id, user.getUsername());
    }

    @PatchMapping("/{id}/toggle")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        service.toggle(id, user.getUsername());
    }
}
