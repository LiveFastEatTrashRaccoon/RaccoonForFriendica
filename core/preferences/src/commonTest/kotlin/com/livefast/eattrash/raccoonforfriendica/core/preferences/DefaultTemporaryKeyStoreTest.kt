package com.livefast.eattrash.raccoonforfriendica.core.preferences

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultTemporaryKeyStoreTest {
    private val settings = mock<SettingsWrapper>(MockMode.autoUnit)

    private val sut = DefaultTemporaryKeyStore(settings)

    @Test
    fun `given non existing key when contains key then interactions are as expected`() {
        every { settings.hasKey(any()) } returns false
        every { settings.keys } returns setOf()

        val res = sut.containsKey("key")

        assertFalse(res)
    }

    @Test
    fun `given existing key when contains key then interactions are as expected`() {
        every { settings.keys } returns setOf("key")

        val res = sut.containsKey("key")

        assertTrue(res)
        verify {
            settings.keys
        }
    }

    @Test
    fun `when get Int then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<Int>()] } returns 2

        val res = sut["key", 1]
        assertEquals(2, res)
        verify {
            settings["key", 1]
        }
    }

    @Test
    fun `when save Int then interactions are as expected`() {
        sut.save("key", 0)

        verify {
            settings["key"] = 0
        }
    }

    @Test
    fun `when get Long then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<Long>()] } returns 2

        val res = sut["key", 1L]
        assertEquals(2L, res)
        verify {
            settings["key", 1L]
        }
    }

    @Test
    fun `when save Long then interactions are as expected`() {
        sut.save("key", 1L)
        verify {
            settings["key"] = 1L
        }
    }

    @Test
    fun `when get Boolean then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<Boolean>()] } returns true

        val res = sut["key", false]
        assertTrue(res)
        verify {
            settings["key", false]
        }
    }

    @Test
    fun `when save Boolean then interactions are as expected`() {
        sut.save("key", true)

        verify {
            settings["key"] = true
        }
    }

    @Test
    fun `when get String then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<String>()] } returns "b"

        val res = sut["key", "a"]
        assertEquals("b", res)
        verify {
            settings["key", "a"]
        }
    }

    @Test
    fun `when save String then interactions are as expected`() {
        sut.save("key", "value")

        verify {
            settings["key"] = "value"
        }
    }

    @Test
    fun `when get Float then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<Float>()] } returns 2.0f

        val res = sut["key", 1.0f]
        assertEquals(2.0f, res)
        verify {
            settings["key", 1.0f]
        }
    }

    @Test
    fun `when save Float then interactions are as expected`() {
        sut.save("key", 1.0f)
        verify {
            settings["key"] = 1.0f
        }
    }

    @Test
    fun `when get Double then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<Double>()] } returns 2.0

        val res = sut["key", 1.0]
        assertEquals(2.0, res)
        verify {
            settings["key", 1.0]
        }
    }

    @Test
    fun `when save Double then interactions are as expected`() {
        sut.save("key", 1.0)

        verify {
            settings["key"] = 1.0
        }
    }

    @Test
    fun `given non existing key when get string list then result is as expected`() {
        every { settings.hasKey(any()) } returns false

        val res = sut.get("key", listOf(""))
        assertEquals(listOf(""), res)
    }

    @Test
    fun `given existing key when get string list then result is as expected`() {
        every { settings.hasKey(any()) } returns true
        every { settings[any(), any<String>()] } returns "a, b"

        val res = sut.get("key", listOf("c", "d"))
        assertEquals(2, res.size)
        assertEquals("a", res.first())
        assertEquals("b", res[1])
        verify {
            settings["key", ""]
        }
    }

    @Test
    fun `when save String list then interactions are as expected`() {
        val values = listOf("a", "b", "c")
        sut.save("key", values)

        verify {
            sut.save(key = "key", value = values, delimiter = ", ")
        }
    }

    @Test
    fun `when remove then interactions are as expected`() {
        sut.remove("key")

        verify {
            settings.remove("key")
        }
    }

    @Test
    fun `when remove all then interactions are as expected`() {
        sut.removeAll()

        verify {
            settings.clear()
        }
    }
}
