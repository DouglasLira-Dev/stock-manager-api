package com.estoque.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de cadastro de novo usuário.
 * Usa validação do Spring para garantir dados consistentes.
 */
public record RegisterRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String password
) {}