package com.estoque.api.dto;

import com.estoque.api.model.Role;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {}