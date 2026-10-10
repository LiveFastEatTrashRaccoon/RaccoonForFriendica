package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultToggleTranslationUseCaseTest {
    private val getTranslation = mock<GetTranslationUseCase>()
    private val sut = DefaultToggleTranslationUseCase(getTranslation)

    @Test
    fun `given entry showing translation when invoked then it returns entry showing original`() = runTest {
        val targetEntry = TimelineEntryModel(id = "1", content = "translated")
        val entry = TimelineEntryModel(
            id = "1",
            content = "original",
            isShowingTranslation = true,
            translation = targetEntry,
            translationProvider = "Provider",
        )

        val result = sut(entry = entry, targetLang = "en")

        assertEquals(
            expected = entry.copy(
                isShowingTranslation = false,
                translationLoading = false,
            ),
            actual = result,
        )
        verifySuspend(VerifyMode.not) {
            getTranslation(any(), any())
        }
    }

    @Test
    fun `given original entry not translated when invoked then it returns entry showing translation`() = runTest {
        val entry = TimelineEntryModel(
            id = "1",
            content = "original",
            isShowingTranslation = false,
            translation = null,
            translationProvider = null,
        )
        val targetEntry = TimelineEntryModel(id = "1", content = "translated")
        val expectedTranslationResult = TranslatedTimelineEntryModel(
            source = entry,
            target = targetEntry,
            provider = "Provider",
        )

        everySuspend { getTranslation(entry, "en") } returns expectedTranslationResult

        val result = sut(entry = entry, targetLang = "en")

        assertEquals(
            expected = entry.copy(
                isShowingTranslation = true,
                translation = targetEntry,
                translationProvider = "Provider",
                translationLoading = false,
            ),
            actual = result,
        )
        verifySuspend {
            getTranslation(entry, "en")
        }
    }

    @Test
    fun `given original entry not translated when translation fails then it returns entry showing original`() =
        runTest {
            val entry = TimelineEntryModel(
                id = "1",
                content = "original",
                isShowingTranslation = false,
                translation = null,
                translationProvider = null,
            )

            everySuspend { getTranslation(entry, "en") } returns null

            val result = sut(entry = entry, targetLang = "en")

            assertEquals(
                expected = entry.copy(
                    isShowingTranslation = false,
                    translation = null,
                    translationProvider = null,
                    translationLoading = false,
                ),
                actual = result,
            )
            verifySuspend {
                getTranslation(entry, "en")
            }
        }

    @Test
    fun `given original entry already translated when invoked then it returns entry showing translation`() = runTest {
        val targetEntry = TimelineEntryModel(id = "1", content = "translated")
        val entry = TimelineEntryModel(
            id = "1",
            content = "original",
            isShowingTranslation = false,
            translation = targetEntry,
            translationProvider = "Provider",
        )

        val result = sut(entry = entry, targetLang = "en")

        assertEquals(
            expected = entry.copy(
                isShowingTranslation = true,
                translationLoading = false,
            ),
            actual = result,
        )
        verifySuspend(VerifyMode.not) {
            getTranslation(any(), any())
        }
    }
}
