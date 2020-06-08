package com.github.aleperaltabazas.rest.core.exception.http;

public class HttpConnectionError extends RuntimeException {
    public HttpConnectionError(String message, Exception cause) {
        super(message, cause);
    }
}