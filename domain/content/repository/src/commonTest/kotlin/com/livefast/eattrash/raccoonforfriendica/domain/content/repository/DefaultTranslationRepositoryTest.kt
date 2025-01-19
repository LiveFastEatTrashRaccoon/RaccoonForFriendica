package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Translation
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultTranslationRepositoryTest {
    private val statusService = mock<StatusService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { statuses } returns statusService
        }
    private val sut =
        DefaultTranslationRepository(
            provider = serviceProvider,
        )

    @Test
    fun `given success when getTranslation then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            val targetContent = "target content"
            val targetLang = "it"
            val provider = "LibreTranslate"
            everySuspend {
                statusService.translate(any(), any())
            } returns Translation(content = targetContent, provider = provider)
            val entry =
                TimelineEntryModel(
                    id = entryId,
                    content = "source content",
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
                statusService.translate(
                    id = entryId,
                    data =
                        matching { arg ->
                            arg.formData["lang"] == targetLang
                        },
                )
            }
        }

    @Test
    fun `given error when getTranslation then result and interactions are as expected`() =
        runTest {
            val entryId = "1"
            val targetLang = "it"
            val provider = "LibreTranslate"
            everySuspend {
                statusService.translate(any(), any())
            } throws IOException("Network error")
            val entry =
                TimelineEntryModel(
                    id = entryId,
                    content = "source content",
                    translationProvider = provider,
                )

            val res =
                sut.getTranslation(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertNull(res)
            verifySuspend {
                statusService.translate(
                    id = entryId,
                    data =
                        matching { arg ->
                            arg.formData["lang"] == targetLang
                        },
                )
            }
        }
}
