package com.github.aleperaltabazas.rest.core.exception.http;

public class UnprocessableEntityException extends RuntimeException {
    public UnprocessableEntityException(String message) {
        super(message);
    }
}