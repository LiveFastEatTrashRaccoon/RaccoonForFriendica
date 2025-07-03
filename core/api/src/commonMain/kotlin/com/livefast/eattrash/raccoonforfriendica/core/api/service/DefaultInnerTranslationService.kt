package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.InnerTranslationApiConfigurationValues
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InnerTranslation
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/*
 * This implementation of InnerTranslationService relies on this free translation API:
 * https://github.com/Uncover-F/TAS
 *
 * BEWARE:
 * It has a usage quota and it should not be used extensively or as a 100% reliable service.
 */
internal class DefaultInnerTranslationService(
    private val baseUrl: String = InnerTranslationApiConfigurationValues.URL,
    private val client: HttpClient,
    private val json: Json,
) : InnerTranslationService {
    override suspend fun translate(sourceText: String, sourceLang: String, targetLang: String): Result<String> =
        runCatching {
            val response =
                client.get(baseUrl) {
                    parameter("text", sourceText)
                    parameter("source_lang", sourceLang)
                    parameter("target_lang", targetLang)
                }
            val body = response.bodyAsText()
            val parsedResult = json.decodeFromString<InnerTranslation>(body)
            parsedResult.response.translatedText
        }
}
