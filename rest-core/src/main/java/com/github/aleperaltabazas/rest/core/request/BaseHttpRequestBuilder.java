package com.github.aleperaltabazas.rest.core.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aleperaltabazas.rest.core.exception.NoSuchHttpMethodException;
import com.github.aleperaltabazas.rest.core.exception.json.JsonSerializationError;
import com.github.aleperaltabazas.rest.core.method.HttpMethod;
import com.github.aleperaltabazas.rest.core.response.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseHttpRequestBuilder {
    private String host;
    private String path;
    private ObjectMapper objectMapper;
    private HttpClient innerHttpClient;
    private HttpMethod method;
    private Map<String, String> headers;
    private Optional<Object> body;

    public BaseHttpRequestBuilder(
        String host,
        String path,
        ObjectMapper objectMapper,
        HttpClient innerHttpClient,
        HttpMethod method
    ) {
        this.host = host;
        this.path = path;
        this.objectMapper = objectMapper;
        this.innerHttpClient = innerHttpClient;
        this.method = method;
        this.headers = Collections.emptyMap();
    }

    public BaseHttpRequestBuilder withHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public BaseHttpRequestBuilder withBody(Object body) {
        this.body = Optional.ofNullable(body);
        return this;
    }

    public HttpResponse execute() {
        try {
            String endpoint = formatEndpoint();
            HttpRequestBase request = requestOf(endpoint, method);
            org.apache.http.HttpResponse apacheResponse = innerHttpClient.execute(request);
            String responseBody = Optional.ofNullable(apacheResponse.getEntity())
                .flatMap(e -> {
                    try {
                        return Optional.ofNullable(e.getContent());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .map(c -> {
                    try {
                        return new BufferedReader(new InputStreamReader(c)).readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse("");
            Map<String, String> responseHeaders = Arrays.stream(apacheResponse.getAllHeaders())
                .collect(Collectors.toMap(Header::getName, Header::getValue));
            Integer responseStatus = apacheResponse.getStatusLine().getStatusCode();
            request.releaseConnection();

            HttpResponse response = new HttpResponse(
                objectMapper,
                responseBody,
                responseStatus,
                responseHeaders
            );

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequestBase requestOf(String endpoint, HttpMethod method) {
        switch (method) {
            case GET:
                return new HttpGet(endpoint);
            case HEAD:
                return new HttpHead(endpoint);
            case DELETE:
                return new HttpDelete(endpoint);
            case OPTIONS:
                return new HttpOptions(endpoint);
            case PATCH:
                return setBody(new HttpPatch(endpoint));
            case POST:
                return setBody(new HttpPost(endpoint));
            case PUT:
                return setBody(new HttpPut(endpoint));
            default:
                throw new NoSuchHttpMethodException(method);
        }
    }

    private HttpEntityEnclosingRequestBase setBody(HttpEntityEnclosingRequestBase request) {
        this.body.ifPresent(b -> {
            try {
                StringEntity params = new StringEntity(objectMapper.writeValueAsString(body),
                    ContentType.APPLICATION_JSON);
                request.setEntity(params);
            } catch (JsonProcessingException e) {
                throw new JsonSerializationError("Failed to serialize request body", e);
            }
        });
        return request;
    }

    private String formatEndpoint() {
        String actualHost = host.endsWith("/") ? host.substring(0, host.length() - 2) : host;
        String actualPath = path.startsWith("/") ? path.substring(1, path.length() - 1) : path;

        return String.format("%s/%s", actualHost, actualPath);
    }
}