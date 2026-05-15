package com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProvider
import com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.dto.TranslationRequestBody
import com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.dto.TranslationResponseBody
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

class LibreTranslateProvider(
    private val baseUrl: String,
    private val apiKey: String? = null,
    factory: HttpClientEngine = provideHttpClientEngine(),
    private val requestTimeout: Long = 600_000,
    private val connectTimeout: Long = 30_000,
) : TranslationProvider {

    private val client = HttpClient(factory) {
        expectSuccess = false
        if (requestTimeout > 0 && connectTimeout > 0) {
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout
                connectTimeoutMillis = connectTimeout
                socketTimeoutMillis = connectTimeout
            }
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    override suspend fun translate(sourceText: String, sourceLang: String, targetLang: String): String = runCatching {
        val response = client.post("$baseUrl/translate") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                TranslationRequestBody(
                    text = sourceText,
                    source = sourceLang,
                    target = targetLang,
                    format = DEFAULT_FORMAT,
                    apiKey = apiKey.orEmpty(),
                ),
            )
        }
        check(response.status.isSuccess())
        val outputData: TranslationResponseBody = response.body()
        outputData.text.orEmpty()
    }.getOrElse { e ->
        if (e is CancellationException) throw e
        ""
    }

    companion object {
        private const val DEFAULT_FORMAT = "text"
    }
}
