package com.estoque.api.exception;

public class InsufficientStockException extends BusinessException {

    private static final String ERROR_CODE = "STOCK_001";

    public InsufficientStockException(Long productId, Integer available, Integer requested) {
        super("Estoque insuficiente para o produto ID " + productId +
              ". Disponível: " + available + ", solicitado: " + requested, ERROR_CODE);
    }
}