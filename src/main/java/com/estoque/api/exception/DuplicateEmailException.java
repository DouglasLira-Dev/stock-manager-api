package com.estoque.api.exception;

/**
 * Exceção lançada quando um email já está cadastrado.
 */
public class DuplicateEmailException extends BusinessException {

    private static final String ERROR_CODE = "USER_002";

    public DuplicateEmailException(String email) {
        super("Email '" + email + "' já está em uso. Por favor, utilize outro email.", ERROR_CODE);
    }
}