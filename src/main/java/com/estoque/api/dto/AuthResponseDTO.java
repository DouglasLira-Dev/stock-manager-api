package com.estoque.api.dto;

/**
 * DTO para resposta de autenticação (login ou cadastro).
 * Contém o token JWT e informações básicas do usuário.
 */
public record AuthResponseDTO(
        String token,
        String email,
        String role
) {}