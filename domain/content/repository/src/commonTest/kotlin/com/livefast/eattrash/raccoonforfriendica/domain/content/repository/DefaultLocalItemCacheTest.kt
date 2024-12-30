package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultLocalItemCacheTest {
    private val cache = mock<LruCache<String, TimelineEntryModel>>(MockMode.autoUnit)
    private val sut = DefaultLocalItemCache(cache = cache)

    @Test
    fun `given item not present when get then result is as expected`() =
        runTest {
            everySuspend { cache.get(any()) } returns null

            val res = sut.get("1")

            assertNull(res)
            verifySuspend {
                cache.get("1")
            }
        }

    @Test
    fun `given item present when get then result is as expected`() =
        runTest {
            val item = TimelineEntryModel(id = "1", content = "")
            everySuspend { cache.get(any()) } returns item

            sut.put(item.id, item)
            val res = sut.get("1")

            assertEquals(item, res)
            verifySuspend {
                cache.get("1")
            }
        }

    @Test
    fun `when remove then interactions are as expected`() =
        runTest {
            sut.remove("1")

            verifySuspend {
                cache.remove("1")
            }
        }

    @Test
    fun `when clear then interactions are as expected`() =
        runTest {
            sut.clear()

            verifySuspend {
                cache.clear()
            }
        }
}
