package com.github.aleperaltabazas.rest.arrow.request

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestBuilder
import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestDecorator
import com.github.aleperaltabazas.rest.core.response.HttpResponse

class HttpRequestBuilder(
    request: BaseHttpRequestBuilder
) : BaseHttpRequestDecorator(request) {
    fun withBody(body: Any?): HttpRequestBuilder {
        this.request.withBody(body)
        return this
    }

    fun withHeader(header: String, value: String): HttpRequestBuilder {
        this.request.withHeader(header, value)
        return this
    }

    fun executeIO(): IO<HttpResponse> = IO { request.execute() }

    fun executeEither(): Either<Throwable, HttpResponse> = try {
        request.execute().right()
    } catch (e: Throwable) {
        e.left()
    }
}