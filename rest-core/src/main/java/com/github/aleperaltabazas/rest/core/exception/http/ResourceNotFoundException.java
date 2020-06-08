package com.github.aleperaltabazas.rest.core.exception.http;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}