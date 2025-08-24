package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.service.InnerTranslationService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
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
            provider = mock {
                every { translation } returns service
            },
        )

    @Test
    fun `given success when getTranslation then result and interactions are as expected`() = runTest {
        val targetContent = "target content"
        val targetLang = "it"
        val provider = "TAS"
        val sourceLang = "en"
        val sourceContent = "source content"
        everySuspend {
            service.translate(any(), any(), any())
        } returns Result.success(targetContent)
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
        assertEquals(sourceLang, res.target.lang)
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
    fun `given entry with title card and spoiler when getTranslation then result and interactions are as expected`() =
        runTest {
            val targetText = "target text"
            val targetLang = "it"
            val provider = "TAS"
            val sourceLang = "en"
            val sourceContent = "source content"
            val sourceTitle = "source title"
            val sourceSpoiler = "source spoiler"
            val sourceCardTitle = "source card title"
            val sourceCardDescription = "source card description"
            everySuspend {
                service.translate(any(), any(), any())
            } returns Result.success(targetText)
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = sourceContent,
                    lang = sourceLang,
                    title = sourceTitle,
                    spoiler = sourceSpoiler,
                    card =
                    PreviewCardModel(
                        title = sourceCardTitle,
                        description = sourceCardDescription,
                    ),
                )

            val res =
                sut.getTranslation(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertNotNull(res)
            assertEquals(targetText, res.target.content)
            assertEquals(targetText, res.target.title)
            assertEquals(targetText, res.target.spoiler)
            assertEquals(
                targetText,
                res.target.card
                    ?.title
                    .orEmpty(),
            )
            assertEquals(
                targetText,
                res.target.card
                    ?.description
                    .orEmpty(),
            )
            assertEquals(sourceLang, res.target.lang)
            assertEquals(provider, res.provider)
            verifySuspend {
                service.translate(
                    sourceText = sourceContent,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                service.translate(
                    sourceText = sourceTitle,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                service.translate(
                    sourceText = sourceSpoiler,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                service.translate(
                    sourceText = sourceCardTitle,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                service.translate(
                    sourceText = sourceCardDescription,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
            }
        }

    @Test
    fun `given entry with poll when getTranslation then result and interactions are as expected`() = runTest {
        val targetText = "target text"
        val targetLang = "it"
        val provider = "TAS"
        val sourceLang = "en"
        val sourceContent = "source content"
        val sourceOption = "source option 1"
        everySuspend {
            service.translate(any(), any(), any())
        } returns Result.success(targetText)
        val entry =
            TimelineEntryModel(
                id = "1",
                content = sourceContent,
                lang = sourceLang,
                poll =
                PollModel(
                    id = "1",
                    options = listOf(PollOptionModel(title = sourceOption)),
                ),
            )

        val res =
            sut.getTranslation(
                entry = entry,
                targetLang = targetLang,
            )

        assertNotNull(res)
        assertEquals(targetText, res.target.content)
        assertEquals(
            targetText,
            res.target.poll
                ?.options
                ?.firstOrNull()
                ?.title
                .orEmpty(),
        )
        assertEquals(sourceLang, res.target.lang)
        assertEquals(provider, res.provider)
        verifySuspend {
            service.translate(
                sourceText = sourceContent,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
            service.translate(
                sourceText = sourceOption,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
        }
    }

    @Test
    fun `given entry with attachments when getTranslation then result and interactions are as expected`() = runTest {
        val targetText = "target text"
        val targetLang = "it"
        val provider = "TAS"
        val sourceLang = "en"
        val sourceContent = "source content"
        val sourceDescription = "source description"
        everySuspend {
            service.translate(any(), any(), any())
        } returns Result.success(targetText)
        val entry =
            TimelineEntryModel(
                id = "1",
                content = sourceContent,
                lang = sourceLang,
                attachments =
                listOf(
                    AttachmentModel(
                        id = "1",
                        url = "",
                        description = sourceDescription,
                    ),
                ),
            )

        val res =
            sut.getTranslation(
                entry = entry,
                targetLang = targetLang,
            )

        assertNotNull(res)
        assertEquals(targetText, res.target.content)
        assertEquals(
            targetText,
            res.target.attachments
                .firstOrNull()
                ?.description
                .orEmpty(),
        )
        assertEquals(sourceLang, res.target.lang)
        assertEquals(provider, res.provider)
        verifySuspend {
            service.translate(
                sourceText = sourceContent,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
            service.translate(
                sourceText = sourceDescription,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
        }
    }

    @Test
    fun `given error when getTranslation then result and interactions are as expected`() = runTest {
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
