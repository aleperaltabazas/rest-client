package com.github.aleperaltabazas.rest.core.exception.http;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}