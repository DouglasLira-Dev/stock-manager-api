package com.estoque.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer minimumQuantity
) {}