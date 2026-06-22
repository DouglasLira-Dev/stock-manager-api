package com.estoque.api.exception;

/**
 * Exceção lançada quando um usuário não é encontrado.
 */
public class UserNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "USER_001";

    public UserNotFoundException(Long id) {
        super("Usuário com ID " + id + " não encontrado", ERROR_CODE);
    }

    public UserNotFoundException(String email) {
        super("Usuário com email " + email + " não encontrado", ERROR_CODE);
    }
}