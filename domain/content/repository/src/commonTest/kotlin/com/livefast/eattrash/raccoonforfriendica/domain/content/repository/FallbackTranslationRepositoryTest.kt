package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.service.InnerTranslationService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FallbackTranslationRepositoryTest {
    private val service = mock<InnerTranslationService>()
    private val sut =
        FallbackTranslationRepository(
            service = service,
        )

    @Test
    fun `given success when getTranslation then result and interactions are as expected`() =
        runTest {
            val targetContent = "target content"
            val targetLang = "it"
            val provider = "TAS"
            val sourceLang = "en"
            val sourceContent = "source content"
            everySuspend {
                service.translate(any(), any(), any())
            } returns targetContent
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = sourceContent,
                    lang = sourceLang,
                )

            val res =
                sut.getTranslation(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertNotNull(res)
            assertEquals(targetContent, res.target.content)
            assertEquals(targetLang, res.target.lang)
            assertEquals(provider, res.provider)
            verifySuspend {
                service.translate(
                    sourceText = sourceContent,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
            }
        }

    @Test
    fun `given error when getTranslation then result and interactions are as expected`() =
        runTest {
            val targetLang = "it"
            val sourceLang = "en"
            val sourceContent = "source content"
            everySuspend {
                service.translate(any(), any(), any())
            } throws IOException("Network error")
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = sourceContent,
                    lang = sourceLang,
                )

            val res =
                sut.getTranslation(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertNull(res)
            verifySuspend {
                service.translate(
                    sourceText = sourceContent,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
            }
        }
}
