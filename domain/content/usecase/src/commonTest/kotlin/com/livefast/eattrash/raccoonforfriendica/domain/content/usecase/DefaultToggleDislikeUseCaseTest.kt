package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import dev.mokkery.answering.calls
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

class DefaultToggleDislikeUseCaseTest {
    private val postRepository = mock<PostRepository>()
    private val sut = DefaultToggleDislikeUseCase(postRepository)

    @Test
    fun `given post not disliked not favorite when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.dislike(any()) } returns true
        val postId = "1"
        val post = PostModel(id = postId, content = "")

        val res = sut(post)

        assertNotNull(res)
        assertEquals(1, res.dislikesCount)
        assertTrue(res.disliked)
        assertFalse(res.favorite)
        verifySuspend {
            postRepository.dislike(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.unfavorite(any())
        }
    }

    @Test
    fun `given dislike error when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.dislike(any()) } returns false
        val postId = "1"
        val post = PostModel(id = postId, content = "")

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.dislike(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.unfavorite(any())
        }
    }

    @Test
    fun `given post not disliked and favorite when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.dislike(any()) } returns true
        everySuspend { postRepository.unfavorite(any()) } calls { args ->
            val postId: String = args.arg(0)
            PostModel(
                id = postId,
                content = "",
                favoriteCount = 0,
                favorite = false,
            )
        }
        val postId = "1"
        val post = PostModel(id = postId, content = "", favorite = true, favoriteCount = 1)

        val res = sut(post)

        assertNotNull(res)
        assertEquals(1, res.dislikesCount)
        assertTrue(res.disliked)
        assertFalse(res.favorite)
        assertEquals(0, res.favoriteCount)
        verifySuspend {
            postRepository.dislike(postId)
            postRepository.unfavorite(postId)
        }
    }

    @Test
    fun `given favorite error when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.dislike(any()) } returns true
        everySuspend { postRepository.unfavorite(any()) } returns null
        val postId = "1"
        val post = PostModel(id = postId, content = "", favorite = true, favoriteCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.unfavorite(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.dislike(postId)
        }
    }

    @Test
    fun `given favorite but dislike error when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.dislike(any()) } returns false
        everySuspend { postRepository.unfavorite(any()) } calls { args ->
            val postId: String = args.arg(0)
            PostModel(
                id = postId,
                content = "",
                favoriteCount = 0,
                favorite = false,
            )
        }
        val postId = "1"
        val post = PostModel(id = postId, content = "", favorite = true, favoriteCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.unfavorite(postId)
            postRepository.dislike(postId)
        }
    }

    @Test
    fun `given post disliked and not favorite when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.undislike(any()) } returns true
        val postId = "1"
        val post = PostModel(id = postId, content = "", disliked = true, dislikesCount = 1)

        val res = sut(post)

        assertNotNull(res)
        assertEquals(0, res.dislikesCount)
        assertFalse(res.disliked)
        assertFalse(res.favorite)
        verifySuspend {
            postRepository.undislike(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.unfavorite(any())
        }
    }

    @Test
    fun `given disliked but dislike error when invoked then result and interactions are as expected`() = runTest {
        everySuspend { postRepository.undislike(any()) } returns false
        val postId = "1"
        val post = PostModel(id = postId, content = "", disliked = true, dislikesCount = 1)

        val res = sut(post)

        assertNull(res)
        verifySuspend {
            postRepository.undislike(postId)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.unfavorite(any())
        }
    }
}
