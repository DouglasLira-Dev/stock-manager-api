package com.estoque.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Nome do produto é obrigatório")
        String name,
        String description,
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity,
        @NotNull(message = "Preço é obrigatório")
        @Positive(message = "Preço deve ser maior que zero")
        BigDecimal price,
        String category,
        Integer minimumQuantity
) {}