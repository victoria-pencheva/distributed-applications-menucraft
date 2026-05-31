package com.recipes.controller;

import com.recipes.dto.PantryItemRequest;
import com.recipes.dto.PantryItemResponse;
import com.recipes.service.PantryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PantryItemResponse> getAll(@AuthenticationPrincipal UserDetails user) {
        return service.getAll(user.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PantryItemResponse upsert(
            @Valid @RequestBody PantryItemRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return service.upsert(req, user.getUsername());
    }

    @PatchMapping("/{id}")
    public PantryItemResponse increment(
            @PathVariable Long id,
            @Valid @RequestBody PantryItemRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return service.increment(id, req, user.getUsername());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll(@AuthenticationPrincipal UserDetails user) {
        service.clearAll(user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        service.delete(id, user.getUsername());
    }
}
