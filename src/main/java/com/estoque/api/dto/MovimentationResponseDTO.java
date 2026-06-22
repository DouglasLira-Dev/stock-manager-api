package com.estoque.api.dto;

import com.estoque.api.model.MovimentationType;
import java.time.LocalDateTime;

public record MovimentationResponseDTO(
        Long id,
        Long productId,
        String productName,
        String userEmail,
        MovimentationType type,
        Integer quantity,
        Integer previousQuantity,
        Integer currentQuantity,
        LocalDateTime createdAt
) {}