package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CustomEmoji
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceService
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultEmojiRepositoryTest {
    private val instanceService = mock<InstanceService>()
    private val serviceProvider =
        mock<ServiceProvider> { every { instance } returns instanceService }
    private val otherServiceProvider =
        mock<ServiceProvider>(MockMode.autoUnit) { every { instance } returns instanceService }
    private val cache = mock<LruCache<String, List<EmojiModel>>>(MockMode.autoUnit)
    private val sut =
        DefaultEmojiRepository(
            provider = serviceProvider,
            otherProvider = otherServiceProvider,
            cache = cache,
        )

    @Test
    fun `given error when getAll then result is as expected`() =
        runTest {
            everySuspend { cache.get(any()) } returns null
            everySuspend { instanceService.getCustomEmojis() } throws IOException("No network")

            val res = sut.getAll(node = null, refresh = false)

            assertTrue(res.isEmpty())
        }

    @Test
    fun `given results and cache miss when getAll on local instance then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } returns null

            val res = sut.getAll(node = null, refresh = false)

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend {
                cache.get("")
                serviceProvider.instance
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                otherServiceProvider.instance
            }
        }

    @Test
    fun `given results and cache hit when getAll on local instance then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { cache.get(any()) } returns list.map { it.toModel() }

            val res = sut.getAll(node = null, refresh = false)

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend {
                cache.get("")
            }
            verifySuspend(VerifyMode.not) {
                serviceProvider.instance
                instanceService.getCustomEmojis()
                otherServiceProvider.instance
            }
        }

    @Test
    fun `given results when getAll on local instance twice then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } sequentiallyReturns
                listOf(null, list.map { it.toModel() })

            sut.getAll()
            val res = sut.getAll()

            assertEquals(list.map { it.toModel() }, res)
            verify {
                serviceProvider.instance
            }
            verifySuspend(VerifyMode.exactly(2)) {
                cache.get("")
            }
            verifySuspend(VerifyMode.exactly(1)) {
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                otherServiceProvider.instance
            }
        }

    @Test
    fun `given results when getAll on local instance twice refreshing then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } sequentiallyReturns
                listOf(null, list.map { it.toModel() })

            sut.getAll()
            val res = sut.getAll(refresh = true)

            assertEquals(list.map { it.toModel() }, res)
            verify {
                serviceProvider.instance
            }
            verifySuspend(VerifyMode.exactly(2)) {
                cache.get("")
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                otherServiceProvider.instance
            }
        }

    @Test
    fun `given results and cache miss when getAll on other instance then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } returns null

            val res = sut.getAll(node = "node")

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend {
                cache.get("node")
                otherServiceProvider.instance
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                serviceProvider.instance
            }
        }

    @Test
    fun `given results and cache hit when getAll on other instance then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { cache.get(any()) } returns list.map { it.toModel() }

            val res = sut.getAll(node = "node")

            assertEquals(list.map { it.toModel() }, res)
            verifySuspend {
                cache.get("node")
            }
            verify(VerifyMode.not) {
                otherServiceProvider.instance
                serviceProvider.instance
            }
            verifySuspend(VerifyMode.not) {
                instanceService.getCustomEmojis()
            }
        }

    @Test
    fun `given results when getAll on other instance twice then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } sequentiallyReturns
                listOf(null, list.map { it.toModel() })

            sut.getAll(node = "node")
            val res = sut.getAll(node = "node")

            assertEquals(list.map { it.toModel() }, res)
            verify {
                otherServiceProvider.instance
            }
            verifySuspend(VerifyMode.exactly(2)) {
                cache.get("node")
            }
            verifySuspend(VerifyMode.exactly(1)) {
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                serviceProvider.instance
            }
        }

    @Test
    fun `given results when getAll on other instance twice refreshing then result is as expected`() =
        runTest {
            val list =
                listOf(
                    CustomEmoji(
                        shortCode = "",
                        url = "",
                        staticUrl = "",
                    ),
                )
            everySuspend { instanceService.getCustomEmojis() } returns list
            everySuspend { cache.get(any()) } returns null

            sut.getAll(node = "node")
            val res = sut.getAll(node = "node", refresh = true)

            assertEquals(list.map { it.toModel() }, res)
            verify {
                otherServiceProvider.instance
            }
            verifySuspend(VerifyMode.exactly(2)) {
                cache.get("node")
                instanceService.getCustomEmojis()
            }
            verify(VerifyMode.not) {
                serviceProvider.instance
            }
        }
}
