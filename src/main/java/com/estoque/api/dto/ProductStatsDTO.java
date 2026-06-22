package com.estoque.api.dto;

import java.math.BigDecimal;

/**
 * DTO para estatísticas do estoque do usuário.
 */
public record ProductStatsDTO(
        Long totalProducts,
        Long totalItemsInStock,
        BigDecimal totalStockValue,
        Long lowStockItems,
        Long outOfStockItems
) {}