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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LibreTranslateProvider(
    private val baseUrl: String,
    private val apiKey: String? = null,
    factory: HttpClientEngine = provideHttpClientEngine(),
) : TranslationProvider {

    private val client = HttpClient(factory) {
        install(HttpTimeout) {
            requestTimeoutMillis = 600_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
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

    override suspend fun translate(sourceText: String, sourceLang: String, targetLang: String): String {
        val inputData = TranslationRequestBody(
            text = sourceText,
            source = sourceLang,
            target = targetLang,
            format = DEFAULT_FORMAT,
            apiKey = apiKey.orEmpty(),
        )
        val response = client.post("$baseUrl/translate") {
            contentType(ContentType.Application.Json)
            setBody(inputData)
        }
        val outputData: TranslationResponseBody = response.body()
        return outputData.text.orEmpty()
    }

    companion object {
        private const val DEFAULT_FORMAT = "text"
    }
}
