package com.estoque.api.exception;

/**
 * Exceção lançada quando um produto não é encontrado.
 */
public class ProductNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "PRODUCT_001";

    public ProductNotFoundException(Long id) {
        super("Produto com ID " + id + " não encontrado", ERROR_CODE);
    }

    public ProductNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
}