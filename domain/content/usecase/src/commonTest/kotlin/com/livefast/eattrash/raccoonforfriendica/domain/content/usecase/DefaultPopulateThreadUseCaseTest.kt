package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
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

class DefaultPopulateThreadUseCaseTest {
    private val postRepository = mock<PostRepository>()
    private val sut =
        DefaultPopulateThreadUseCase(postRepository = postRepository)

    @Test
    fun `given terminal post when invoke then result and interactions are as expected`() = runTest {
        val root =
            PostModel(
                id = "1",
                content = "test",
            )

        val res = sut.invoke(root)

        assertEquals(listOf(root), res)
        verifySuspend(VerifyMode.not) {
            postRepository.getContext(any())
        }
    }

    @Test
    fun `given descendants can not be determined then result has the load more option`() = runTest {
        val root =
            PostModel(
                id = "1",
                content = "test",
                replyCount = 1,
            )
        everySuspend {
            postRepository.getContext(any())
        } returns TimelineContextModel(descendants = listOf())

        val res = sut.invoke(root)

        assertEquals(listOf(root.copy(loadMoreButtonVisible = true)), res)
        verifySuspend {
            postRepository.getContext(root.id)
        }
    }

    @Test
    fun `given max depth less than actual depth when invoke then result is as expected`() = runTest {
        val root =
            PostModel(
                id = "P0",
                content = "root",
                replyCount = 2,
            )
        val child1 =
            PostModel(
                id = "P1",
                content = "reply 1",
                inReplyTo = PostModel(id = root.id, content = ""),
                replyCount = 3,
            )
        val child2 =
            PostModel(
                id = "P2",
                content = "reply 2",
                inReplyTo = PostModel(id = root.id, content = ""),
                replyCount = 2,
            )
        val child11 =
            PostModel(
                id = "P3",
                content = "reply 3",
                inReplyTo = PostModel(id = child1.id, content = ""),
            )
        val child12 =
            PostModel(
                id = "P4",
                content = "reply 4",
                inReplyTo = PostModel(id = child1.id, content = ""),
            )
        val child13 =
            PostModel(
                id = "P5",
                content = "reply 5",
                inReplyTo = PostModel(id = child1.id, content = ""),
                replyCount = 2,
            )
        val child21 =
            PostModel(
                id = "P6",
                content = "reply 6",
                inReplyTo = PostModel(id = child2.id, content = ""),
            )
        val child22 =
            PostModel(
                id = "P7",
                content = "reply 7",
                inReplyTo = PostModel(id = child2.id, content = ""),
            )
        val child131 =
            PostModel(
                id = "P8",
                content = "reply 8",
                inReplyTo = PostModel(id = child13.id, content = ""),
            )
        val child132 =
            PostModel(
                id = "P9",
                content = "reply 9",
                inReplyTo = PostModel(id = child13.id, content = ""),
            )
        everySuspend {
            postRepository.getContext(any())
        } returns TimelineContextModel(
            descendants = listOf(
                child1, child2, child11, child12, child13, child131, child132, child21, child22,
            ),
        )

        val res = sut.invoke(post = root, maxDepth = 2)

        assertEquals(
            listOf(
                root,
                child1.copy(depth = 1),
                child11.copy(depth = 2),
                child12.copy(depth = 2),
                // the pruning point is detected
                child13.copy(depth = 2, loadMoreButtonVisible = true),
                child2.copy(depth = 1),
                child21.copy(depth = 2),
                child22.copy(depth = 2),
            ),
            res,
        )
        verifySuspend {
            postRepository.getContext(root.id)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.getContext(child1.id)
            postRepository.getContext(child2.id)
            postRepository.getContext(child11.id)
            postRepository.getContext(child12.id)
            postRepository.getContext(child13.id)
            postRepository.getContext(child131.id)
            postRepository.getContext(child132.id)
            postRepository.getContext(child21.id)
            postRepository.getContext(child22.id)
        }
    }

    @Test
    fun `given structure with reblogs when invoke then result is as expected`() = runTest {
        val root =
            PostModel(
                id = "P0",
                content = "",
                reblog = PostModel(
                    id = "P0",
                    content = "root",
                    replyCount = 2,
                ),
            )
        val child1 =
            PostModel(
                id = "P1",
                content = "",
                reblog = PostModel(
                    id = "P1",
                    content = "reply 1",
                    inReplyTo = PostModel(id = root.id, content = ""),
                    replyCount = 3,
                ),
            )
        val child2 =
            PostModel(
                id = "P2",
                content = "",
                reblog = PostModel(
                    id = "P2",
                    content = "reply 2",
                    inReplyTo = PostModel(id = root.id, content = ""),
                    replyCount = 2,
                ),
            )
        val child11 =
            PostModel(
                id = "P3",
                content = "reply 3",
                reblog = PostModel(
                    id = "P3",
                    content = "reply 3",
                    inReplyTo = PostModel(id = child1.id, content = ""),
                ),
            )
        val child12 =
            PostModel(
                id = "P4",
                content = "",
                reblog = PostModel(
                    id = "P4",
                    content = "reply 4",
                    inReplyTo = PostModel(id = child1.id, content = ""),
                ),
            )
        val child13 =
            PostModel(
                id = "P5",
                content = "",
                reblog = PostModel(
                    id = "P5",
                    content = "reply 5",
                    inReplyTo = PostModel(id = child1.id, content = ""),
                    replyCount = 2,
                ),
            )
        val child21 =
            PostModel(
                id = "P6",
                content = "",
                reblog = PostModel(
                    id = "P6",
                    content = "reply 6",
                    inReplyTo = PostModel(id = child2.id, content = ""),
                ),
            )
        val child22 =
            PostModel(
                id = "P7",
                content = "",
                reblog = PostModel(
                    id = "P7",
                    content = "reply 7",
                    inReplyTo = PostModel(id = child2.id, content = ""),
                ),
            )
        val child131 =
            PostModel(
                id = "P8",
                content = "",
                reblog = PostModel(
                    id = "P8",
                    content = "reply 8",
                    inReplyTo = PostModel(id = child13.id, content = ""),
                ),
            )
        val child132 =
            PostModel(
                id = "P9",
                content = "",
                reblog = PostModel(
                    id = "P9",
                    content = "reply 9",
                    inReplyTo = PostModel(id = child13.id, content = ""),
                ),
            )
        everySuspend {
            postRepository.getContext(any())
        } returns TimelineContextModel(
            descendants = listOf(
                child1, child2, child11, child12, child13, child131, child132, child21, child22,
            ),
        )

        val res = sut.invoke(post = root, maxDepth = 2)

        assertEquals(
            listOf(
                root.original,
                child1.original.copy(depth = 1),
                child11.original.copy(depth = 2),
                child12.original.copy(depth = 2),
                child13.original.copy(depth = 2, loadMoreButtonVisible = true),
                child2.original.copy(depth = 1),
                child21.original.copy(depth = 2),
                child22.original.copy(depth = 2),
            ),
            res,
        )
    }

    @Test
    fun `given further invocation on partial tree when invoke then result is as expected`() = runTest {
        val root =
            PostModel(
                id = "P0",
                content = "root",
                replyCount = 2,
            )
        val child1 =
            PostModel(
                id = "P1",
                content = "reply 1",
                inReplyTo = PostModel(id = root.id, content = ""),
                replyCount = 3,
            )
        val child2 =
            PostModel(
                id = "P2",
                content = "reply 2",
                inReplyTo = PostModel(id = root.id, content = ""),
                replyCount = 2,
            )
        val child11 =
            PostModel(
                id = "P3",
                content = "reply 3",
                inReplyTo = PostModel(id = child1.id, content = ""),
            )
        val child12 =
            PostModel(
                id = "P4",
                content = "reply 4",
                inReplyTo = PostModel(id = child1.id, content = ""),
            )
        val child13 =
            PostModel(
                id = "P5",
                content = "reply 5",
                inReplyTo = PostModel(id = child1.id, content = ""),
                replyCount = 2,
            )
        val child21 =
            PostModel(
                id = "P6",
                content = "reply 6",
                inReplyTo = PostModel(id = child2.id, content = ""),
            )
        val child22 =
            PostModel(
                id = "P7",
                content = "reply 7",
                inReplyTo = PostModel(id = child2.id, content = ""),
            )
        val child131 =
            PostModel(
                id = "P8",
                content = "reply 8",
                inReplyTo = PostModel(id = child13.id, content = ""),
            )
        val child132 =
            PostModel(
                id = "P9",
                content = "reply 9",
                inReplyTo = PostModel(id = child13.id, content = ""),
            )
        everySuspend {
            postRepository.getContext(any())
        } calls {
            val postId = it.arg<String>(0)
            when (postId) {
                root.id ->
                    TimelineContextModel(
                        descendants = listOf(
                            child1, child2, child11, child12, child13, child131, child132, child21, child22,
                        ),
                    )

                child13.id ->
                    TimelineContextModel(
                        ancestors = listOf(child1),
                        descendants = listOf(child131, child132),
                    )

                else -> TimelineContextModel()
            }
        }
        val prunedTree = sut.invoke(post = root, maxDepth = 2)
        val pruningIndex = prunedTree.indexOfFirst { post -> post.loadMoreButtonVisible }
        val subroot = prunedTree[pruningIndex]

        val res = sut.invoke(post = subroot, maxDepth = 2)

        assertEquals(
            listOf(
                subroot.copy(loadMoreButtonVisible = false),
                child131.copy(depth = 3),
                child132.copy(depth = 3),
            ),
            res,
        )
        verifySuspend {
            postRepository.getContext(child13.id)
        }
        verifySuspend(VerifyMode.not) {
            postRepository.getContext(child131.id)
            postRepository.getContext(child132.id)
        }
    }
}
