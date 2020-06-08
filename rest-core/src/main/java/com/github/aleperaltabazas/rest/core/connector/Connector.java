package com.github.aleperaltabazas.rest.core.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aleperaltabazas.rest.core.exception.http.*;
import com.github.aleperaltabazas.rest.core.exception.json.JsonDeserializationError;
import com.github.aleperaltabazas.rest.core.exception.json.JsonSerializationError;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Connector {
    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);

    private HttpClient apacheHttpClient;
    private ObjectMapper objectMapper;
    private String host;
    private Map<String, String> defaultHeaders;

    public Connector(ObjectMapper objectMapper, String host) {
        this.apacheHttpClient = HttpClientBuilder.create().build();
        this.objectMapper = objectMapper;
        this.defaultHeaders = new HashMap<>();
        this.host = host;
    }

    public Connector(ObjectMapper objectMapper, String host, Map<String, String> defaultHeaders) {
        this.objectMapper = objectMapper;
        this.host = host;
        this.defaultHeaders = defaultHeaders;
        this.apacheHttpClient = HttpClientBuilder.create().build();
    }

    public <T> T get(String endpoint, TypeReference<T> typeReference) {
        String target = formatEndpoint(endpoint);
        LOGGER.info("GET request to {}", target);

        HttpGet get = new HttpGet(target);
        return execute(get, typeReference);
    }

    public <T> T post(String endpoint, TypeReference<T> typeReference, Object body) {
        String target = formatEndpoint(endpoint);
        LOGGER.info("POST request to {}", target);

        HttpPost post = new HttpPost(target);
        setBody(post, body);
        return this.execute(post, typeReference);
    }

    private void setBody(HttpPost request, Object body) {
        try {
            StringEntity params = new StringEntity(objectMapper.writeValueAsString(body), ContentType.APPLICATION_JSON);
            request.setEntity(params);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error serializing {}", body);
            throw new JsonSerializationError("Error serializing " + body, e);
        }
    }

    private <T> T execute(HttpRequestBase request, TypeReference<T> typeReference) {
        try {
            HttpResponse response = apacheHttpClient.execute(request);
            LOGGER.info("Response {}", response);

            checkStatus(request, response);

            T json = this.deserializeAs(response, typeReference);
            request.releaseConnection();
            return json;
        } catch (IOException e) {
            LOGGER.warn("Error executing request {}", request, e);
            throw new HttpConnectionError("Error executing request " + request, e);
        }
    }

    private <T> T deserializeAs(HttpResponse response, TypeReference<T> typeReference) {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String body = rd.readLine();

            T value = objectMapper.readValue(body, typeReference);

            LOGGER.info("Deserialized as {}", value);

            return value;
        } catch (IOException e) {
            LOGGER.warn("Error deserializing response {}", response);
            throw new JsonDeserializationError("Error deserializing json.", e);
        }
    }

    private void checkStatus(HttpRequest request, HttpResponse response) {
        switch (response.getStatusLine().getStatusCode()) {
            case 400:
                LOGGER.info("Got bad request from other end on request {}. Response {}", request, response);
                throw new BadRequestException("Bad request: " + request + ", response: " + response);
            case 401:
                LOGGER.info("Unauthorized request {}. Response {}", request, response);
                throw new UnauthorizedException("Unauthorized: " + request + ", response: " + response);
            case 403:
                LOGGER.info("Forbidden request {}. Response {}", request, response);
                throw new ForbiddenException("Forbidden: " + request + ", response: " + response);
            case 404:
                LOGGER.info("Resource not found request {}. Response {}", request, response);
                throw new ResourceNotFoundException("Resource not found: " + request + ", response: " + response);
            case 422:
                LOGGER.info("Unprocessable entity request {}. Response {}", request, response);
                throw new UnprocessableEntityException("Unprocessable entity: " + request + ", response " + response);
            case 500:
                LOGGER.info("Internal Server Error request {}. Response {}", request, response);
                throw new InternalServerErrorException("Internal Server Error: " + request + ", response: " + response);
            default:
                LOGGER.info("Status code {}", response.getStatusLine().getStatusCode());
        }
    }

    private String formatEndpoint(String endpoint) {
        return String.format("%s/%s", host, endpoint);
    }
}