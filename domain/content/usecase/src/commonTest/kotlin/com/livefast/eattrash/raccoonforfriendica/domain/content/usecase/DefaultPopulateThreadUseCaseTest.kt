package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultPopulateThreadUseCaseTest {
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<TimelineEntryModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val timelineEntryRepository = mock<TimelineEntryRepository>()
    private val sut =
        DefaultPopulateThreadUseCase(
            timelineEntryRepository = timelineEntryRepository,
            emojiHelper = emojiHelper,
        )

    @Test
    fun `given terminal entry when invoke then result and interactions are as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "1",
                content = "test",
            )

        val res = sut.invoke(root)

        assertEquals(listOf(root), res)
        verifySuspend(VerifyMode.not) {
            timelineEntryRepository.getContext(any())
        }
    }

    @Test
    fun `given descendants can not be determined then result has the load more option`() = runTest {
        val root =
            TimelineEntryModel(
                id = "1",
                content = "test",
                replyCount = 1,
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } returns TimelineContextModel(descendants = listOf())

        val res = sut.invoke(root)

        assertEquals(listOf(root.copy(loadMoreButtonVisible = true)), res)
        verifySuspend {
            timelineEntryRepository.getContext(root.id)
        }
    }

    @Test
    fun `given max depth less than actual depth when invoke then result is as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "P0",
                content = "root",
                replyCount = 2,
            )
        val child1 =
            TimelineEntryModel(
                id = "P1",
                content = "reply 1",
                inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                replyCount = 3,
            )
        val child2 =
            TimelineEntryModel(
                id = "P2",
                content = "reply 2",
                inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                replyCount = 2,
            )
        val child11 =
            TimelineEntryModel(
                id = "P3",
                content = "reply 3",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
            )
        val child12 =
            TimelineEntryModel(
                id = "P4",
                content = "reply 4",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
            )
        val child13 =
            TimelineEntryModel(
                id = "P5",
                content = "reply 5",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
                replyCount = 2,
            )
        val child21 =
            TimelineEntryModel(
                id = "P6",
                content = "reply 6",
                inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
            )
        val child22 =
            TimelineEntryModel(
                id = "P7",
                content = "reply 7",
                inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
            )
        val child131 =
            TimelineEntryModel(
                id = "P8",
                content = "reply 8",
                inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
            )
        val child132 =
            TimelineEntryModel(
                id = "P9",
                content = "reply 9",
                inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } returns TimelineContextModel(
            descendants = listOf(
                child1, child2, child11, child12, child13, child131, child132, child21, child22,
            ),
        )

        val res = sut.invoke(entry = root, maxDepth = 2)

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
            timelineEntryRepository.getContext(root.id)
        }
        verifySuspend(VerifyMode.not) {
            timelineEntryRepository.getContext(child1.id)
            timelineEntryRepository.getContext(child2.id)
            timelineEntryRepository.getContext(child11.id)
            timelineEntryRepository.getContext(child12.id)
            timelineEntryRepository.getContext(child13.id)
            timelineEntryRepository.getContext(child131.id)
            timelineEntryRepository.getContext(child132.id)
            timelineEntryRepository.getContext(child21.id)
            timelineEntryRepository.getContext(child22.id)
        }
    }

    @Test
    fun `given structure with reblogs when invoke then result is as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "P0",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P0",
                    content = "root",
                    replyCount = 2,
                ),
            )
        val child1 =
            TimelineEntryModel(
                id = "P1",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P1",
                    content = "reply 1",
                    inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                    replyCount = 3,
                ),
            )
        val child2 =
            TimelineEntryModel(
                id = "P2",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P2",
                    content = "reply 2",
                    inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                    replyCount = 2,
                ),
            )
        val child11 =
            TimelineEntryModel(
                id = "P3",
                content = "reply 3",
                reblog = TimelineEntryModel(
                    id = "P3",
                    content = "reply 3",
                    inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
                ),
            )
        val child12 =
            TimelineEntryModel(
                id = "P4",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P4",
                    content = "reply 4",
                    inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
                ),
            )
        val child13 =
            TimelineEntryModel(
                id = "P5",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P5",
                    content = "reply 5",
                    inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
                    replyCount = 2,
                ),
            )
        val child21 =
            TimelineEntryModel(
                id = "P6",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P6",
                    content = "reply 6",
                    inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
                ),
            )
        val child22 =
            TimelineEntryModel(
                id = "P7",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P7",
                    content = "reply 7",
                    inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
                ),
            )
        val child131 =
            TimelineEntryModel(
                id = "P8",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P8",
                    content = "reply 8",
                    inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
                ),
            )
        val child132 =
            TimelineEntryModel(
                id = "P9",
                content = "",
                reblog = TimelineEntryModel(
                    id = "P9",
                    content = "reply 9",
                    inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
                ),
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } returns TimelineContextModel(
            descendants = listOf(
                child1, child2, child11, child12, child13, child131, child132, child21, child22,
            ),
        )

        val res = sut.invoke(entry = root, maxDepth = 2)

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
    fun `given further invocation on partial tree, when invoke then result is as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "P0",
                content = "root",
                replyCount = 2,
            )
        val child1 =
            TimelineEntryModel(
                id = "P1",
                content = "reply 1",
                inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                replyCount = 3,
            )
        val child2 =
            TimelineEntryModel(
                id = "P2",
                content = "reply 2",
                inReplyTo = TimelineEntryModel(id = root.id, content = ""),
                replyCount = 2,
            )
        val child11 =
            TimelineEntryModel(
                id = "P3",
                content = "reply 3",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
            )
        val child12 =
            TimelineEntryModel(
                id = "P4",
                content = "reply 4",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
            )
        val child13 =
            TimelineEntryModel(
                id = "P5",
                content = "reply 5",
                inReplyTo = TimelineEntryModel(id = child1.id, content = ""),
                replyCount = 2,
            )
        val child21 =
            TimelineEntryModel(
                id = "P6",
                content = "reply 6",
                inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
            )
        val child22 =
            TimelineEntryModel(
                id = "P7",
                content = "reply 7",
                inReplyTo = TimelineEntryModel(id = child2.id, content = ""),
            )
        val child131 =
            TimelineEntryModel(
                id = "P8",
                content = "reply 8",
                inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
            )
        val child132 =
            TimelineEntryModel(
                id = "P9",
                content = "reply 9",
                inReplyTo = TimelineEntryModel(id = child13.id, content = ""),
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } calls {
            val entryId = it.arg<String>(0)
            when (entryId) {
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
        val prunedTree = sut.invoke(entry = root, maxDepth = 2)
        val pruningIndex = prunedTree.indexOfFirst { e -> e.loadMoreButtonVisible }
        val subroot = prunedTree[pruningIndex]

        val res = sut.invoke(entry = subroot, maxDepth = 2)

        assertEquals(
            listOf(
                subroot.copy(loadMoreButtonVisible = false),
                child131.copy(depth = 3),
                child132.copy(depth = 3),
            ),
            res,
        )
        verifySuspend {
            timelineEntryRepository.getContext(child13.id)
        }
        verifySuspend(VerifyMode.not) {
            timelineEntryRepository.getContext(child131.id)
            timelineEntryRepository.getContext(child132.id)
        }
    }
}
