package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProvider
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultFallbackTranslationRepositoryTest {
    private val translationProvider = mock<TranslationProvider> {
        everySuspend { translate(sourceText = any(), sourceLang = any(), targetLang = any()) } returnsArgAt 0
    }

    private val translationProviderFactory = mock<TranslationProviderFactory> {
        every { create(any()) } returns translationProvider
    }

    private val sut = DefaultFallbackTranslationRepository(translationProviderFactory)

    @Test
    fun `given success when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = TranslationProviderConfig(name = "DUMMY", url = "")
        val sourceLang = "en"
        val sourceContent = "source content"
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
                config = config,
            )

        assertNotNull(res)
        assertEquals(sourceContent, res.target.content)
        assertEquals(sourceLang, res.target.lang)
        assertEquals(config.name, res.provider)
        verifySuspend {
            translationProviderFactory.create(config)
            translationProvider.translate(
                sourceText = sourceContent,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
        }
    }

    @Test
    fun `given entry with title card and spoiler when getTranslation then result and interactions are as expected`() =
        runTest {
            val targetLang = "it"
            val config = TranslationProviderConfig(name = "DUMMY", url = "")
            val sourceLang = "en"
            val sourceTitle = "source title"
            val sourceSpoiler = "source spoiler"
            val sourceCardTitle = "source card title"
            val sourceCardDescription = "source card description"
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "",
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
                    config = config,
                )

            assertNotNull(res)
            assertEquals(sourceTitle, res.target.title)
            assertEquals(sourceSpoiler, res.target.spoiler)
            assertEquals(
                sourceCardTitle,
                res.target.card
                    ?.title
                    .orEmpty(),
            )
            assertEquals(
                sourceCardDescription,
                res.target.card
                    ?.description
                    .orEmpty(),
            )
            assertEquals(sourceLang, res.target.lang)
            assertEquals(config.name, res.provider)
            verifySuspend {
                translationProviderFactory.create(config)
                translationProvider.translate(
                    sourceText = sourceTitle,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                translationProvider.translate(
                    sourceText = sourceSpoiler,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                translationProvider.translate(
                    sourceText = sourceCardTitle,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
                translationProvider.translate(
                    sourceText = sourceCardDescription,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                )
            }
        }

    @Test
    fun `given entry with poll when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = TranslationProviderConfig(name = "DUMMY", url = "")
        val sourceLang = "en"
        val sourceOption = "source option 1"
        val entry =
            TimelineEntryModel(
                id = "1",
                content = "",
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
                config = config,
            )

        assertNotNull(res)
        assertEquals(
            sourceOption,
            res.target.poll
                ?.options
                ?.firstOrNull()
                ?.title
                .orEmpty(),
        )
        assertEquals(sourceLang, res.target.lang)
        assertEquals(config.name, res.provider)
        verifySuspend {
            translationProviderFactory.create(config)
            translationProvider.translate(
                sourceText = sourceOption,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
        }
    }

    @Test
    fun `given entry with attachments when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = TranslationProviderConfig(name = "DUMMY", url = "")
        val sourceLang = "en"
        val sourceDescription = "source description"
        val entry =
            TimelineEntryModel(
                id = "1",
                content = "",
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
                config = config,
            )

        assertNotNull(res)
        assertEquals(
            sourceDescription,
            res.target.attachments
                .firstOrNull()
                ?.description
                .orEmpty(),
        )
        assertEquals(sourceLang, res.target.lang)
        assertEquals(config.name, res.provider)
        verifySuspend {
            translationProviderFactory.create(config)
            translationProvider.translate(
                sourceText = sourceDescription,
                sourceLang = sourceLang,
                targetLang = targetLang,
            )
        }
    }
}
