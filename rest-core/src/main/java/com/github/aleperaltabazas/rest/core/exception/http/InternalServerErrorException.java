package com.github.aleperaltabazas.rest.core.exception.http;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}