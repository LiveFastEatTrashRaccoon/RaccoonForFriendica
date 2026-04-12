package com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LibreTranslateProviderTest {

    private val baseUrl = "https://libretranslate.com"
    private val apiKey = "fake-api-key"

    @Test
    fun `given success response when translate then return translated text`() = runTest {
        val sourceText = "Hello"
        val sourceLang = "en"
        val targetLang = "it"
        val expectedTranslation = "Ciao"

        val mockEngine = MockEngine {
            respond(
                content = """{"translatedText":"$expectedTranslation"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val sut = LibreTranslateProvider(
            baseUrl = baseUrl,
            apiKey = apiKey,
            factory = mockEngine,
        )

        val result = sut.translate(sourceText, sourceLang, targetLang)

        assertEquals(expectedTranslation, result)
    }

    @Test
    fun `given forbidden response when translate then return empty string`() = runTest {
        val sourceText = "Hello"
        val sourceLang = "en"
        val targetLang = "it"

        val mockEngine = MockEngine {
            respond(
                content = "{message: \"exceeded quota\"}",
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val sut = LibreTranslateProvider(
            baseUrl = baseUrl,
            apiKey = apiKey,
            factory = mockEngine,
        )

        val result = sut.translate(sourceText, sourceLang, targetLang)

        assertEquals("", result)
    }
}
