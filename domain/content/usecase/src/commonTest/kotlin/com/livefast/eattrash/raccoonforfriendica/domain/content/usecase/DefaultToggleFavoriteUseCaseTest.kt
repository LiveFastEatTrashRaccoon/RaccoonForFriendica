package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultToggleFavoriteUseCaseTest {
    private val postRepository = mock<PostRepository>()
    private val sut = DefaultToggleFavoriteUseCase(postRepository)

    @Test
    fun `given post not favorite and not disliked when invoked then result and interactions are as expected`() =
        runTest {
            val postId = "1"
            everySuspend { postRepository.favorite(any()) } returns
                PostModel(
                    id = postId,
                    content = "",
                    favorite = true,
                    favoriteCount = 1,
                )
            val post = PostModel(id = postId, content = "")

            val res = sut(post)

            assertNotNull(res)
            assertEquals(1, res.favoriteCount)
            assertTrue(res.favorite)
            assertFalse(res.disliked)
            verifySuspend {
                postRepository.favorite(postId)
            }
            verifySuspend(VerifyMode.not) {
                postRepository.undislike(any())
            }
        }

    @Test
    fun `given favorite error when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.favorite(any()) } returns null
        val post = PostModel(id = postId, content = "")

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.favorite(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.undislike(any())
        }
    }

    @Test
    fun `given post not favorite and disliked when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.favorite(any()) } returns
            PostModel(
                id = postId,
                content = "",
                favorite = true,
                favoriteCount = 1,
            )
        everySuspend { postRepository.undislike(any()) } returns true
        val post = PostModel(id = postId, content = "", disliked = true, dislikesCount = 1)

        val res = sut(post)

        assertNotNull(res)
        assertEquals(1, res.favoriteCount)
        assertTrue(res.favorite)
        assertFalse(res.disliked)
        verifySuspend {
            postRepository.favorite(postId)
            postRepository.undislike(postId)
        }
    }

    @Test
    fun `given disliked but favorite error when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.favorite(any()) } returns null
        everySuspend { postRepository.undislike(any()) } returns true
        val post = PostModel(id = postId, content = "", disliked = true, dislikesCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.favorite(postId)
            postRepository.undislike(postId)
        }
    }

    @Test
    fun `given disliked but dislike error when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.favorite(any()) } returns
            PostModel(
                id = postId,
                content = "",
                favorite = true,
                favoriteCount = 1,
            )
        everySuspend { postRepository.undislike(any()) } returns false
        val post = PostModel(id = postId, content = "", disliked = true, dislikesCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.undislike(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.favorite(any())
        }
    }

    @Test
    fun `given post favorite and not disliked when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.unfavorite(any()) } returns
            PostModel(id = postId, content = "")
        val post = PostModel(id = postId, content = "", favorite = true, favoriteCount = 1)

        val res = sut(post)

        assertNotNull(res)
        assertEquals(0, res.favoriteCount)
        assertFalse(res.favorite)
        assertFalse(res.disliked)
        verifySuspend {
            postRepository.unfavorite(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.undislike(any())
        }
    }

    @Test
    fun `given post favorite but favorite error when invoked then result and interactions are as expected`() = runTest {
        val postId = "1"
        everySuspend { postRepository.unfavorite(any()) } returns null
        val post = PostModel(id = postId, content = "", favorite = true, favoriteCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.unfavorite(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.undislike(any())
        }
    }
}
