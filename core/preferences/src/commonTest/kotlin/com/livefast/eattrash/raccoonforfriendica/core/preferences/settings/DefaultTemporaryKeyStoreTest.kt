package com.livefast.eattrash.raccoonforfriendica.core.preferences.settings

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.DefaultTemporaryKeyStore
import com.russhwolf.settings.Settings
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultTemporaryKeyStoreTest {
    private val settings = mock<Settings>(MockMode.autoUnit)

    private val sut = DefaultTemporaryKeyStore(settings)

    @Test
    fun `given non existing key when contains key then interactions are as expected`() = runTest {
        every { settings.hasKey(any()) } returns false
        every { settings.keys } returns setOf()

        val res = sut.containsKey("key")

        assertFalse(res)
    }

    @Test
    fun `given existing key when contains key then interactions are as expected`() = runTest {
        every { settings.keys } returns setOf("key")

        val res = sut.containsKey("key")

        assertTrue(res)
        verify {
            settings.keys
        }
    }

    @Test
    fun `when get Int then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getInt(any(), any()) } returns 2

        val res = sut.get("key", 1)
        assertEquals(2, res)
        verify {
            settings.getInt("key", 1)
        }
    }

    @Test
    fun `when save Int then interactions are as expected`() = runTest {
        sut.save("key", 0)

        verify {
            settings.putInt("key", 0)
        }
    }

    @Test
    fun `when get Long then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getLong(any(), any()) } returns 2L

        val res = sut.get("key", 1L)
        assertEquals(2L, res)
        verify {
            settings.getLong("key", 1L)
        }
    }

    @Test
    fun `when save Long then interactions are as expected`() = runTest {
        sut.save("key", 1L)
        verify {
            settings.putLong("key", 1L)
        }
    }

    @Test
    fun `when get Boolean then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getBoolean(any(), any()) } returns true

        val res = sut.get("key", false)
        assertTrue(res)
        verify {
            settings.getBoolean("key", false)
        }
    }

    @Test
    fun `when save Boolean then interactions are as expected`() = runTest {
        sut.save("key", true)

        verify {
            settings.putBoolean("key", true)
        }
    }

    @Test
    fun `when get String then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getString(any(), any()) } returns "b"

        val res = sut.get("key", "a")
        assertEquals("b", res)
        verify {
            settings.getString("key", "a")
        }
    }

    @Test
    fun `when save String then interactions are as expected`() = runTest {
        sut.save("key", "value")

        verify {
            settings.putString("key", "value")
        }
    }

    @Test
    fun `when get Float then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getFloat(any(), any()) } returns 2.0f

        val res = sut.get("key", 1.0f)
        assertEquals(2.0f, res)
        verify {
            settings.getFloat("key", 1.0f)
        }
    }

    @Test
    fun `when save Float then interactions are as expected`() = runTest {
        sut.save("key", 1.0f)
        verify {
            settings.putFloat("key", 1.0f)
        }
    }

    @Test
    fun `when get Double then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getDouble(any(), any()) } returns 2.0

        val res = sut.get("key", 1.0)
        assertEquals(2.0, res)
        verify {
            settings.getDouble("key", 1.0)
        }
    }

    @Test
    fun `when save Double then interactions are as expected`() = runTest {
        sut.save("key", 1.0)

        verify {
            settings.putDouble("key", 1.0)
        }
    }

    @Test
    fun `given non existing key when get String list then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns false

        val res = sut.get("key", listOf(""))
        assertEquals(listOf(""), res)
    }

    @Test
    fun `given existing key when get String list then result is as expected`() = runTest {
        every { settings.hasKey(any()) } returns true
        every { settings.getString(any(), any()) } returns "a, b"

        val res = sut.get("key", listOf("c", "d"))
        assertEquals(2, res.size)
        assertEquals("a", res.first())
        assertEquals("b", res[1])
        verify {
            settings.getString("key", "")
        }
    }

    @Test
    fun `when save String list then interactions are as expected`() = runTest {
        val values = listOf("a", "b", "c")
        sut.save("key", values)

        verify {
            settings.putString("key", values.joinToString(", "))
        }
    }

    @Test
    fun `when remove then interactions are as expected`() = runTest {
        sut.remove("key")

        verify {
            settings.remove("key")
        }
    }

    @Test
    fun `when remove all then interactions are as expected`() = runTest {
        sut.removeAll()

        verify {
            settings.clear()
        }
    }
}
