package com.recipes.auth;

import com.recipes.dto.*;
import com.recipes.entity.*;
import com.recipes.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.recipes.entity.User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already registered");
        com.recipes.entity.User user = new com.recipes.entity.User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(req.username()), req.username(), Role.USER.name());
    }

    public AuthResponse login(LoginRequest req) {
        com.recipes.entity.User user = userRepository.findByUsername(req.username())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");
        return new AuthResponse(jwtUtil.generateToken(user.getUsername()),
            user.getUsername(), user.getRole().name());
    }
}
