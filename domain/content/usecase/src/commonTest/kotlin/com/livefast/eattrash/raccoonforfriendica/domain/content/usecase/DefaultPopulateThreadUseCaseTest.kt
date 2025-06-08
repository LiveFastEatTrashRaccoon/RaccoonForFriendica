package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
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
    fun `given max depth reached when invoke then result and interactions are as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "1",
                content = "test",
                replyCount = 1,
            )
        val child1 =
            TimelineEntryModel(
                id = "2",
                content = "reply 1",
                replyCount = 1,
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } returns
            TimelineContextModel(
                ancestors = listOf(root),
                descendants = listOf(child1),
            )

        val res = sut.invoke(entry = root, maxDepth = 1)

        assertEquals(
            listOf(
                root,
                child1.copy(depth = 1, loadMoreButtonVisible = true),
            ),
            res,
        )
        verifySuspend {
            timelineEntryRepository.getContext("1")
        }
        verifySuspend(VerifyMode.not) {
            timelineEntryRepository.getContext("2")
        }
    }

    @Test
    fun `when invoke then result and interactions are as expected`() = runTest {
        val root =
            TimelineEntryModel(
                id = "1",
                content = "test",
                replyCount = 1,
            )
        val child1 =
            TimelineEntryModel(
                id = "2",
                content = "reply 1",
                replyCount = 1,
            )
        val child2 =
            TimelineEntryModel(
                id = "3",
                content = "reply 2",
            )
        everySuspend {
            timelineEntryRepository.getContext(any())
        } calls {
            val entryId = it.arg<String>(0)
            when (entryId) {
                root.id ->
                    TimelineContextModel(
                        ancestors = listOf(root),
                        descendants = listOf(child1),
                    )

                child1.id ->
                    TimelineContextModel(
                        ancestors = listOf(child1),
                        descendants = listOf(child2),
                    )

                else -> TimelineContextModel()
            }
        }

        val res = sut.invoke(entry = root)

        assertEquals(
            listOf(
                root,
                child1.copy(depth = 1),
                child2.copy(depth = 2),
            ),
            res,
        )
        verifySuspend {
            timelineEntryRepository.getContext("1")
            timelineEntryRepository.getContext("2")
        }
        verifySuspend(VerifyMode.not) {
            timelineEntryRepository.getContext("3")
        }
    }
}
