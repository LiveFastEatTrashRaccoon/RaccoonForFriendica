package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
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
    private val entryRepository = mock<TimelineEntryRepository>()
    private val entryCache = mock<LruCache<String, TimelineEntryModel>>(MockMode.autoUnit)
    private val sut =
        DefaultReplyHelper(
            entryRepository = entryRepository,
            entryCache = entryCache,
        )

    @Test
    fun `given no parent when withInReplyToIfMissing then result is as expected`() = runTest {
        val entry = TimelineEntryModel(id = "1", content = "content")

        val res =
            with(sut) {
                entry.withInReplyToIfMissing()
            }

        assertEquals(entry, res)
        verifySuspend(VerifyMode.not) {
            entryRepository.getById(any())
            entryCache.get(any())
            entryCache.put(any(), any())
        }
    }

    @Test
    fun `given parent with content when withInReplyToIfMissing then result is as expected`() = runTest {
        val parent =
            TimelineEntryModel(
                id = "0",
                content = "parent content",
            )
        val entry =
            TimelineEntryModel(
                id = "1",
                content = "content",
                inReplyTo = parent,
            )

        val res =
            with(sut) {
                entry.withInReplyToIfMissing()
            }

        assertEquals(entry, res)
        verifySuspend(VerifyMode.not) {
            entryRepository.getById(any())
            entryCache.get(any())
            entryCache.put(any(), any())
        }
    }

    @Test
    fun `given parent with empty content and cache hit when withInReplyToIfMissing then result is as expected`() =
        runTest {
            val emptyParent =
                TimelineEntryModel(
                    id = "0",
                    content = "",
                )
            val fullParent =
                TimelineEntryModel(
                    id = "0",
                    content = "parent content",
                )
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "content",
                    inReplyTo = emptyParent,
                )
            everySuspend {
                entryCache.get("0")
            } returns fullParent

            val res =
                with(sut) {
                    entry.withInReplyToIfMissing()
                }

            assertEquals(entry.copy(inReplyTo = fullParent), res)
            verifySuspend {
                entryCache.get("0")
            }
            verifySuspend(VerifyMode.not) {
                entryRepository.getById(any())
                entryCache.put(any(), any())
            }
        }

    @Test
    fun `given parent with empty content and cache miss when withInReplyToIfMissing then result is as expected`() =
        runTest {
            val emptyParent =
                TimelineEntryModel(
                    id = "0",
                    content = "",
                )
            val fullParent =
                TimelineEntryModel(
                    id = "0",
                    content = "parent content",
                )
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "content",
                    inReplyTo = emptyParent,
                )
            everySuspend {
                entryCache.get("0")
            } returns null
            everySuspend {
                entryRepository.getById("0")
            } returns fullParent

            val res =
                with(sut) {
                    entry.withInReplyToIfMissing()
                }

            assertEquals(entry.copy(inReplyTo = fullParent), res)
            verifySuspend {
                entryCache.get("0")
                entryRepository.getById("0")
                entryCache.put("0", fullParent)
            }
        }
}
