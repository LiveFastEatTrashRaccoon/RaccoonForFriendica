package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel
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
    fun `given post showing translation when invoked then it returns post showing original`() = runTest {
        val targetPost = PostModel(id = "1", content = "translated")
        val post = PostModel(
            id = "1",
            content = "original",
            isShowingTranslation = true,
            translation = targetPost,
            translationProvider = "Provider",
        )

        val result = sut(post = post, targetLang = "en")

        assertEquals(
            expected = post.copy(
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
    fun `given original post not translated when invoked then it returns post showing translation`() = runTest {
        val post = PostModel(
            id = "1",
            content = "original",
            isShowingTranslation = false,
            translation = null,
            translationProvider = null,
        )
        val targetPost = PostModel(id = "1", content = "translated")
        val expectedTranslationResult = TranslatedPostModel(
            source = post,
            target = targetPost,
            provider = "Provider",
        )

        everySuspend { getTranslation(post, "en") } returns expectedTranslationResult

        val result = sut(post = post, targetLang = "en")

        assertEquals(
            expected = post.copy(
                isShowingTranslation = true,
                translation = targetPost,
                translationProvider = "Provider",
                translationLoading = false,
            ),
            actual = result,
        )
        verifySuspend {
            getTranslation(post, "en")
        }
    }

    @Test
    fun `given original post not translated when translation fails then it returns post showing original`() = runTest {
        val post = PostModel(
            id = "1",
            content = "original",
            isShowingTranslation = false,
            translation = null,
            translationProvider = null,
        )

        everySuspend { getTranslation(post, "en") } returns null

        val result = sut(post = post, targetLang = "en")

        assertEquals(
            expected = post.copy(
                isShowingTranslation = false,
                translation = null,
                translationProvider = null,
                translationLoading = false,
            ),
            actual = result,
        )
        verifySuspend {
            getTranslation(post, "en")
        }
    }

    @Test
    fun `given original post already translated when invoked then it returns post showing translation`() = runTest {
        val targetPost = PostModel(id = "1", content = "translated")
        val post = PostModel(
            id = "1",
            content = "original",
            isShowingTranslation = false,
            translation = targetPost,
            translationProvider = "Provider",
        )

        val result = sut(post = post, targetLang = "en")

        assertEquals(
            expected = post.copy(
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
