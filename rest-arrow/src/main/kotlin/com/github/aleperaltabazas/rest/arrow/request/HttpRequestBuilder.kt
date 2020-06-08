package com.github.aleperaltabazas.rest.arrow.request

import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestBuilder
import com.github.aleperaltabazas.rest.core.request.BaseHttpRequestDecorator
import com.github.aleperaltabazas.rest.core.response.HttpResponse

class HttpRequestBuilder(
    request: BaseHttpRequestBuilder
) : BaseHttpRequestDecorator(request)  {
}