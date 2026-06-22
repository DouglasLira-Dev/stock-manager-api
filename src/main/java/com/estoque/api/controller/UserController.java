package com.estoque.api.controller;

import com.estoque.api.dto.UserResponseDTO;
import com.estoque.api.model.User;
import com.estoque.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para operações relacionadas ao usuário autenticado.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retorna os dados do usuário atualmente autenticado.
     * Obtém o email do contexto de segurança e busca no banco.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        // Obtém o email do usuário autenticado (definido no JwtAuthenticationFilter)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }
}