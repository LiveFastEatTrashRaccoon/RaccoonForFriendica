package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TagsService
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultTagRepositoryTest {
    private val tagService = mock<TagsService>()
    private val provider = mock<ServiceProvider> {
        every { tag } returns tagService
    }
    private val sut = DefaultTagRepository(provider)

    @Test
    fun `given results when getFollowed then result and interactions are as expected`() = runTest {
        val tags = listOf(Tag(name = "tag1", url = "url1"))
        everySuspend { tagService.getFollowedTags(any()) } returns (tags to "cursor")

        val res = sut.getFollowed(null)

        assertNotNull(res)
        assertEquals(1, res.list.size)
        assertEquals("tag1", res.list.first().name)
        assertEquals("cursor", res.cursor)
        verifySuspend { tagService.getFollowedTags(null) }
    }

    @Test
    fun `given results when getBy then result and interactions are as expected`() = runTest {
        val tag = Tag(name = "tag1", url = "url1")
        everySuspend { tagService.get(any()) } returns tag

        val res = sut.getBy("tag1")

        assertNotNull(res)
        assertEquals("tag1", res.name)
        verifySuspend { tagService.get("tag1") }
    }

    @Test
    fun `given results when follow then result and interactions are as expected`() = runTest {
        val tag = Tag(name = "tag1", url = "url1", following = true)
        everySuspend { tagService.follow(any()) } returns tag

        val res = sut.follow("tag1")

        assertNotNull(res)
        assertEquals("tag1", res.name)
        assertEquals(true, res.following)
        verifySuspend { tagService.follow("tag1") }
    }

    @Test
    fun `given results when unfollow then result and interactions are as expected`() = runTest {
        val tag = Tag(name = "tag1", url = "url1", following = false)
        everySuspend { tagService.unfollow(any()) } returns tag

        val res = sut.unfollow("tag1")

        assertNotNull(res)
        assertEquals("tag1", res.name)
        assertEquals(false, res.following)
        verifySuspend { tagService.unfollow("tag1") }
    }
}
