package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultReplyHelperTest {
    private val postRepository = mock<PostRepository>()
    private val postCache = mock<LruCache<String, PostModel>>(MockMode.autoUnit)
    private val sut =
        DefaultReplyHelper(
            postRepository = postRepository,
            postCache = postCache,
        )

    @Test
    fun `given no parent when withInReplyToIfMissing then result is as expected`() = runTest {
        val post = PostModel(id = "1", content = "content")

        val res =
            with(sut) {
                post.withInReplyToIfMissing()
            }

        assertEquals(post, res)
        verifySuspend(VerifyMode.not) {
            postRepository.getById(any())
            postCache.get(any())
            postCache.put(any(), any())
        }
    }

    @Test
    fun `given parent with content when withInReplyToIfMissing then result is as expected`() = runTest {
        val parent =
            PostModel(
                id = "0",
                content = "parent content",
            )
        val post =
            PostModel(
                id = "1",
                content = "content",
                inReplyTo = parent,
            )

        val res =
            with(sut) {
                post.withInReplyToIfMissing()
            }

        assertEquals(post, res)
        verifySuspend(VerifyMode.not) {
            postRepository.getById(any())
            postCache.get(any())
            postCache.put(any(), any())
        }
    }

    @Test
    fun `given parent with empty content and cache hit when withInReplyToIfMissing then result is as expected`() =
        runTest {
            val emptyParent =
                PostModel(
                    id = "0",
                    content = "",
                )
            val fullParent =
                PostModel(
                    id = "0",
                    content = "parent content",
                )
            val post =
                PostModel(
                    id = "1",
                    content = "content",
                    inReplyTo = emptyParent,
                )
            everySuspend {
                postCache.get("0")
            } returns fullParent

            val res =
                with(sut) {
                    post.withInReplyToIfMissing()
                }

            assertEquals(post.copy(inReplyTo = fullParent), res)
            verifySuspend {
                postCache.get("0")
            }
            verifySuspend(VerifyMode.not) {
                postRepository.getById(any())
                postCache.put(any(), any())
            }
        }

    @Test
    fun `given parent with empty content and cache miss when withInReplyToIfMissing then result is as expected`() =
        runTest {
            val emptyParent =
                PostModel(
                    id = "0",
                    content = "",
                )
            val fullParent =
                PostModel(
                    id = "0",
                    content = "parent content",
                )
            val post =
                PostModel(
                    id = "1",
                    content = "content",
                    inReplyTo = emptyParent,
                )
            everySuspend {
                postCache.get("0")
            } returns null
            everySuspend {
                postRepository.getById("0")
            } returns fullParent

            val res =
                with(sut) {
                    post.withInReplyToIfMissing()
                }

            assertEquals(post.copy(inReplyTo = fullParent), res)
            verifySuspend {
                postCache.get("0")
                postRepository.getById("0")
                postCache.put("0", fullParent)
            }
        }
}
