package com.estoque.api.exception;

import java.time.LocalDateTime;

/**
 * DTO padronizado para respostas de erro da API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String errorCode
) {

    public ErrorResponse(int status, String error, String message, String path, String errorCode) {
        this(LocalDateTime.now(), status, error, message, path, errorCode);
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path);
    }

    public static ErrorResponse of(int status, String error, String message, String path, String errorCode) {
        return new ErrorResponse(status, error, message, path, errorCode);
    }
}