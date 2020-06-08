package com.github.aleperaltabazas.rest.core.exception.json;

public class JsonSerializationError extends RuntimeException {
    public JsonSerializationError(String message, Exception cause) {
        super(message, cause);
    }
}