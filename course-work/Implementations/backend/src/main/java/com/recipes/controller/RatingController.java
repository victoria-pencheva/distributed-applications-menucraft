package com.recipes.controller;

import com.recipes.dto.RatingRequest;
import com.recipes.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Void> upsert(@PathVariable Long recipeId,
                                       @Valid @RequestBody RatingRequest req,
                                       @AuthenticationPrincipal UserDetails user) {
        ratingService.upsert(recipeId, req, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long recipeId,
                                       @AuthenticationPrincipal UserDetails user) {
        ratingService.delete(recipeId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
