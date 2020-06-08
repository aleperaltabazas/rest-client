package com.github.aleperaltabazas.rest.core.exception.json;

public class JsonDeserializationError extends RuntimeException {
    public JsonDeserializationError(String message, Exception cause) {
        super(message, cause);
    }
}