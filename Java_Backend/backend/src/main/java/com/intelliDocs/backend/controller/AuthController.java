package com.intelliDocs.backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelliDocs.backend.dto.auth.AuthResponse;
import com.intelliDocs.backend.dto.auth.LoginRequest;
import com.intelliDocs.backend.dto.auth.RegisterRequest;
import com.intelliDocs.backend.entity.User;
import com.intelliDocs.backend.repository.UserRepository;
import com.intelliDocs.backend.security.JWTService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JWTService jwtService;

    public AuthController(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.builder()
                            .message("Email already exists")
                            .build());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole() == null || request.getRole().isBlank() ? "USER" : request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponse.builder()
                        .message("User registered successfully")
                        .role(savedUser.getRole())
                        .token(token)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || !request.getPassword().equals(userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("Invalid email or password")
                            .build());
        }

        User user = userOpt.get();

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .message("Login successful")
                .role(user.getRole())
                .token(token)
                .build());
    }
}
