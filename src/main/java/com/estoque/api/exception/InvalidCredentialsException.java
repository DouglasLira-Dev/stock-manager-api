package com.estoque.api.exception;

/**
 * Exceção lançada quando as credenciais de login são inválidas.
 */
public class InvalidCredentialsException extends BusinessException {

    private static final String ERROR_CODE = "AUTH_001";

    public InvalidCredentialsException() {
        super("Email ou senha inválidos. Verifique suas credenciais.", ERROR_CODE);
    }
}