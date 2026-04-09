package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultFallbackTranslationRepositoryTest {
    private val sut = DefaultFallbackTranslationRepository()

    @Test
    fun `given success when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = FallbackTranslationProviderConfig(name = "DUMMY", url = "", apiKey = "")
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
    }

    @Test
    fun `given entry with title card and spoiler when getTranslation then result and interactions are as expected`() =
        runTest {
            val targetLang = "it"
            val config = FallbackTranslationProviderConfig(name = "DUMMY", url = "", apiKey = "")
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
        }

    @Test
    fun `given entry with poll when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = FallbackTranslationProviderConfig(name = "DUMMY", url = "", apiKey = "")
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
    }

    @Test
    fun `given entry with attachments when getTranslation then result and interactions are as expected`() = runTest {
        val targetLang = "it"
        val config = FallbackTranslationProviderConfig(name = "DUMMY", url = "", apiKey = "")
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
    }
}
