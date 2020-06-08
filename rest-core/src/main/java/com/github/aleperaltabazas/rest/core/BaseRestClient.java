package com.github.aleperaltabazas.rest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aleperaltabazas.rest.core.method.HttpMethod;
import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestBuilder;
import com.github.aleperaltabazas.rest.core.request.HttpRequestDecorator;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collections;
import java.util.Map;

public abstract class BaseRestClient<T extends HttpRequestDecorator> {
    private HttpClient innerHttpClient;
    private ObjectMapper objectMapper;
    private String host;
    private Map<String, String> defaultHeaders;

    public BaseRestClient(ObjectMapper objectMapper, String host) {
        this(HttpClientBuilder.create().build(), objectMapper, host, Collections.emptyMap());
    }

    public BaseRestClient(
        HttpClient innerHttpClient,
        ObjectMapper objectMapper,
        String host,
        Map<String, String> defaultHeaders
    ) {
        this.innerHttpClient = innerHttpClient;
        this.objectMapper = objectMapper;
        this.host = host;
        this.defaultHeaders = defaultHeaders;
    }

    public T get(String path) {
        return requestWithMethod(path, HttpMethod.GET);
    }

    public T post(String path) {
        return requestWithMethod(path, HttpMethod.POST);
    }

    public T patch(String path) {
        return requestWithMethod(path, HttpMethod.PATCH);
    }

    public T put(String path) {
        return requestWithMethod(path, HttpMethod.PUT);
    }

    public T delete(String path) {
        return requestWithMethod(path, HttpMethod.DELETE);
    }

    public T options(String path) {
        return requestWithMethod(path, HttpMethod.OPTIONS);
    }

    public T head(String path) {
        return requestWithMethod(path, HttpMethod.HEAD);
    }

    private T requestWithMethod(String path, HttpMethod method) {
        BaseHttpRequestBuilder httpRequestBuilder = new BaseHttpRequestBuilder(
            host,
            path,
            objectMapper,
            innerHttpClient,
            method
        );

        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            httpRequestBuilder.withHeader(header.getKey(), header.getValue());
        }

        return this.decorate(httpRequestBuilder);
    }

    protected abstract T decorate(BaseHttpRequestBuilder request);
}