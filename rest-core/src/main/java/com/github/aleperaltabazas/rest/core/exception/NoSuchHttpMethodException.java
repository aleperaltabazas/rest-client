package com.github.aleperaltabazas.rest.core.exception;

import com.github.aleperaltabazas.rest.core.method.HttpMethod;

public class NoSuchHttpMethodException extends RuntimeException {
    public NoSuchHttpMethodException(HttpMethod method) {
        super("No such method " + method);
    }
}
