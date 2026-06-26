package com.estoque.api.integration;

import com.estoque.api.dto.AuthRequestDTO;
import com.estoque.api.dto.RegisterRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginSuccessfully() {
        // Register
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "João Teste",
                "joao@teste.com",
                "123456"
        );
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                "/auth/register",
                registerRequest,
                String.class
        );
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        // Login
        AuthRequestDTO loginRequest = new AuthRequestDTO("joao@teste.com", "123456");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                String.class
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertTrue(loginResponse.getBody().contains("token"));
    }
}