package com.intelliDocs.backend.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.intelliDocs.backend.entity.User;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistAndRetrieveUserByEmail() {
        User user = User.builder()
                .name("Ada Lovelace")
                .email("ada@example.com")
                .password("hashed-password")
                .role("ADMIN")
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();

        Optional<User> foundUser = userRepository.findByEmail("ada@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Ada Lovelace");
    }
}
