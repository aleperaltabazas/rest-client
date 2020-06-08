package com.github.aleperaltabazas.rest.core.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aleperaltabazas.rest.core.exception.json.JsonDeserializationError;

import java.util.Map;

public class HttpResponse {
    private ObjectMapper objectMapper;
    private String body;
    private Integer status;
    private Map<String, String> responseHeaders;

    public Boolean isError() {
        return this.status >= 400 && this.status <= 599;
    }

    public HttpResponse(
        ObjectMapper objectMapper,
        String body,
        Integer status,
        Map<String, String> responseHeaders
    ) {
        this.objectMapper = objectMapper;
        this.body = body;
        this.status = status;
        this.responseHeaders = responseHeaders;
    }

    public <T> T deserialize(TypeReference<T> as) {
        try {
            return objectMapper.readValue(body, as);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationError("Failed to deserialize response body", e);
        }
    }

    public String getBody() {
        return body;
    }

    public Integer getStatus() {
        return status;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }
}