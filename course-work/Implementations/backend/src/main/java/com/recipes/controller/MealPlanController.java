package com.recipes.controller;

import com.recipes.dto.MealPlanEntryRequest;
import com.recipes.dto.MealPlanEntryResponse;
import com.recipes.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-plan")
public class MealPlanController {

    private final MealPlanService service;

    public MealPlanController(MealPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<MealPlanEntryResponse> getWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @AuthenticationPrincipal UserDetails user) {
        return service.getWeek(user.getUsername(), weekStart);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanEntryResponse add(
            @Valid @RequestBody MealPlanEntryRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return service.add(req, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        service.delete(id, user.getUsername());
    }

    @PatchMapping("/{id}/servings")
    public ResponseEntity<MealPlanEntryResponse> updateServings(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.updateServings(id, body.get("servings"), user.getUsername()));
    }
}
