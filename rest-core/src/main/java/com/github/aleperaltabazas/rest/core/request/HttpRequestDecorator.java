package com.github.aleperaltabazas.rest.core.request;

public abstract class HttpRequestDecorator {
    protected BaseHttpRequestBuilder request;

    public HttpRequestDecorator(BaseHttpRequestBuilder request) {
        this.request = request;
    }
}