package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.InnerTranslationApiConfigurationValues
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InnerTranslation
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/*
 * This implementation of InnerTranslationService relies on this free translation API:
 * https://github.com/Uncover-F/TAS
 *
 * BEWARE:
 * It has a usage quota and it should not be used extensively or as a 100% reliable service.
 */
internal class DefaultInnerTranslationService(private val client: HttpClient = HttpClient(provideHttpClientEngine())) :
    InnerTranslationService {
    override suspend fun translate(sourceText: String, sourceLang: String, targetLang: String): String? = runCatching {
        val response =
            client.get {
                url(InnerTranslationApiConfigurationValues.URL)
                parameter("text", sourceText)
                parameter("source_lang", sourceLang)
                parameter("target_lang", targetLang)
            }
        val body = response.bodyAsText()
        val parsedResult = Json.decodeFromString<InnerTranslation>(body)
        parsedResult.response.translatedText
    }.getOrNull()
}
