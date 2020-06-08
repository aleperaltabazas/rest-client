package com.github.aleperaltabazas.rest.core.exception.http;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}