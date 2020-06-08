package com.github.aleperaltabazas.rest.core.exception.http;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}