package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import de.jensklingenberg.ktorfit.Response
import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlin.coroutines.CoroutineContext
import kotlin.test.fail

internal fun <T> mockResponse(statusCode: HttpStatusCode = HttpStatusCode.OK, res: T? = null): Response<T> {
    val rawResponse =
        object : HttpResponse() {
            override val call: HttpClientCall
                get() = fail("not implemented")
            override val coroutineContext: CoroutineContext
                get() = fail("not implemented")
            override val headers: Headers
                get() = fail("not implemented")

            @InternalAPI
            override val rawContent: ByteReadChannel
                get() = fail("not implemented")
            override val requestTime: GMTDate
                get() = fail("not implemented")
            override val responseTime: GMTDate
                get() = fail("not implemented")
            override val status: HttpStatusCode
                get() = statusCode
            override val version: HttpProtocolVersion
                get() = fail("not implemented")
        }
    val response =
        if (res != null) {
            Response.success<T>(
                body = res,
                rawResponse = rawResponse,
            )
        } else {
            Response.error(
                body = object {},
                rawResponse = rawResponse,
            )
        }

    @Suppress("UNCHECKED_CAST")
    return response as Response<T>
}
