package com.estoque.api.exception;

/**
 * Exceção base para erros de negócio da aplicação.
 * Todas as exceções customizadas devem estender esta classe.
 */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}