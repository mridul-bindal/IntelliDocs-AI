package com.intelliDocs.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.intelliDocs.backend.dto.auth.AuthResponse;
import com.intelliDocs.backend.dto.auth.RegisterRequest;
import com.intelliDocs.backend.entity.User;
import com.intelliDocs.backend.repository.UserRepository;
import com.intelliDocs.backend.security.JWTService;

class AuthControllerTest {

    @Test
    void shouldRegisterUserAndReturnToken() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        JWTService jwtService = new JWTService("LssUntjoDwqFhzcS5K8X4zGiIzj5OX9K", 86400000L);
        AuthController controller = new AuthController(userRepository, jwtService);

        RegisterRequest request = RegisterRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .password("password123")
                .build();

        Mockito.when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        ResponseEntity<AuthResponse> response = controller.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User registered successfully");
        assertThat(response.getBody().getToken()).isNotBlank();
    }
}
