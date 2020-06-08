package com.github.aleperaltabazas.rest.arrow.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.aleperaltabazas.rest.arrow.request.HttpRequestBuilder
import com.github.aleperaltabazas.rest.core.BaseRestClient
import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestBuilder
import org.apache.http.client.HttpClient

class RestClient(
    innerHttpClient: HttpClient,
    objectMapper: ObjectMapper,
    host: String,
    defaultHeaders: MutableMap<String, String>
) : BaseRestClient<HttpRequestBuilder>(innerHttpClient, objectMapper, host, defaultHeaders) {
    override fun decorate(request: BaseHttpRequestBuilder): HttpRequestBuilder = HttpRequestBuilder(request)
}