package com.estoque.api.exception;

/**
 * Exceção lançada quando um usuário tenta acessar um produto que não lhe pertence.
 */
public class UnauthorizedProductAccessException extends BusinessException {

    private static final String ERROR_CODE = "PRODUCT_002";

    public UnauthorizedProductAccessException(Long productId, Long userId) {
        super("Usuário com ID " + userId + " não tem permissão para acessar o produto com ID " + productId, ERROR_CODE);
    }

    public UnauthorizedProductAccessException(String message) {
        super(message, ERROR_CODE);
    }
}