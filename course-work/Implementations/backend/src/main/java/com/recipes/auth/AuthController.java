package com.recipes.auth;

import com.recipes.dto.*;
import com.recipes.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/check-username")
    public java.util.Map<String, Boolean> checkUsername(@RequestParam String username) {
        return java.util.Map.of("available", !userRepository.existsByUsername(username));
    }

    @GetMapping("/check-email")
    public java.util.Map<String, Boolean> checkEmail(@RequestParam String email) {
        return java.util.Map.of("available", !userRepository.existsByEmail(email));
    }
}
