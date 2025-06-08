package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultEmojiHelperTest {
    private val emojiRepository = mock<EmojiRepository>()
    private val sut =
        DefaultEmojiHelper(
            repository = emojiRepository,
        )

    @Test
    fun `given user already has emojis when withEmojisIfMissing then result is as expected`() = runTest {
        val emojis =
            listOf(
                EmojiModel("test1", "url1", ""),
                EmojiModel("test2", "url2", ""),
            )
        val user =
            UserModel(
                id = "1",
                handle = "user@$DOMAIN",
                displayName = "name :test1:",
                bio = "bio :test2:",
                emojis = emojis,
            )

        val res =
            with(sut) {
                user.withEmojisIfMissing()
            }

        assertEquals(2, res.emojis.size)
        verifySuspend(VerifyMode.not) {
            emojiRepository.getAll(node = any())
        }
    }

    @Test
    fun `given user is missing emojis when withEmojisIfMissing then result is as expected`() = runTest {
        val emojis =
            listOf(
                EmojiModel("test1", "url1", ""),
                EmojiModel("test2", "url2", ""),
                EmojiModel("test3", "url3", ""),
            )
        everySuspend { emojiRepository.getAll(any()) } returns emojis
        val user =
            UserModel(
                id = "1",
                handle = "user@$DOMAIN",
                displayName = "name :test1:",
                bio = "bio :test2:",
            )

        val res =
            with(sut) {
                user.withEmojisIfMissing()
            }

        assertEquals(2, res.emojis.size)
        verifySuspend {
            emojiRepository.getAll(node = DOMAIN)
        }
    }

    @Test
    fun `given post is missing creator emojis when withEmojisIfMissing then result is as expected`() = runTest {
        val emojis =
            listOf(
                EmojiModel("test1", "url1", ""),
                EmojiModel("test2", "url2", ""),
                EmojiModel("test3", "url3", ""),
            )
        everySuspend { emojiRepository.getAll(any()) } returns emojis
        val user =
            UserModel(
                id = "1",
                handle = "user@$DOMAIN",
                displayName = "name :test1:",
                bio = "bio :test2:",
            )
        val entry =
            TimelineEntryModel(
                creator = user,
                id = "1",
                content = "post text",
            )

        val res =
            with(sut) {
                entry.withEmojisIfMissing()
            }

        assertEquals(2, res.creator?.emojis?.size)
        verifySuspend {
            emojiRepository.getAll(node = DOMAIN)
        }
    }

    @Test
    fun `given post missing reblog creator emojis when withEmojisIfMissing then result is as expected`() = runTest {
        val emojis =
            listOf(
                EmojiModel("test1", "url1", ""),
                EmojiModel("test2", "url2", ""),
                EmojiModel("test3", "url3", ""),
            )
        everySuspend { emojiRepository.getAll(any()) } returns emojis
        val user =
            UserModel(
                id = "1",
                handle = "user@$DOMAIN",
                displayName = "name :test1:",
                bio = "bio :test2:",
            )
        val originalEntry =
            TimelineEntryModel(
                creator = user,
                id = "1",
                content = "post text",
            )
        val entry =
            TimelineEntryModel(
                id = "1",
                content = "post text",
                reblog = originalEntry,
            )

        val res =
            with(sut) {
                entry.withEmojisIfMissing()
            }

        assertEquals(
            2,
            res.reblog
                ?.creator
                ?.emojis
                ?.size,
        )
        verifySuspend {
            emojiRepository.getAll(node = DOMAIN)
        }
    }

    @Test
    fun `given post is missing poll option emojis when withEmojisIfMissing then result is as expected`() = runTest {
        val emojis =
            listOf(
                EmojiModel("test1", "url1", ""),
                EmojiModel("test2", "url2", ""),
                EmojiModel("test3", "url3", ""),
            )
        everySuspend { emojiRepository.getAll(any()) } returns emojis
        val user =
            UserModel(
                id = "1",
                handle = "user@$DOMAIN",
            )
        val entry =
            TimelineEntryModel(
                id = "1",
                content = "post text",
                creator = user,
                poll =
                PollModel(
                    id = "1",
                    options =
                    listOf(
                        PollOptionModel("option :test1:"),
                    ),
                ),
            )

        val res =
            with(sut) {
                entry.withEmojisIfMissing()
            }

        assertEquals(1, res.emojis.size)
        verifySuspend {
            emojiRepository.getAll(node = DOMAIN)
        }
    }

    companion object {
        private const val DOMAIN = "example.com"
    }
}
